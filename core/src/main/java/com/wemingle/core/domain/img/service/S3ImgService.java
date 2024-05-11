package com.wemingle.core.domain.img.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImgService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    private static final String MEMBER_PATH = "profile/member/";
    private static final String TEAM_POST_PATH = "post/team/";
    private static final String TEAM_MEMBER_PATH = "profile/group/";
    private static final String TEAM_PATH = "team/";
    private static final String TEAM_BACKGROUND_PATH = "team/background";
    private static final Duration EXPIRY_TIME = Duration.ofMinutes(1L);

    public boolean isAvailableExtension(String extension) {
        try {
            AllowExtensions.valueOf(extension);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAvailableExtensions(List<String> extensions) {
        return extensions.stream().distinct().toList().stream()
                .map(this::isAvailableExtension)
                .allMatch(isAvailable -> isAvailable.equals(true));
    }

    public String getMemberProfilePicUrl(UUID picId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(MEMBER_PATH + picId).build();
        GetObjectPresignRequest objectPreSignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(EXPIRY_TIME).build();
        URL url = s3Presigner.presignGetObject(objectPreSignRequest).url();
        s3Presigner.close();
        return url.toString();
    }
    public String setMemberProfilePreSignedUrl(UUID memberProfileUUID) {
        PutObjectRequest getObjectRequest = PutObjectRequest.builder().bucket(bucket).key(MEMBER_PATH + memberProfileUUID).build();
        PutObjectPresignRequest objectPreSignRequest = PutObjectPresignRequest.builder().signatureDuration(EXPIRY_TIME).putObjectRequest(getObjectRequest).build();
        URL url = s3Presigner.presignPutObject(objectPreSignRequest).url();
        s3Presigner.close();
        return url.toString();
    }

    public String setGroupProfilePreSignedUrl(UUID groupProfileUUID) {
        PutObjectRequest getObjectRequest = PutObjectRequest.builder().bucket(bucket).key(TEAM_PATH + groupProfileUUID).build();
        PutObjectPresignRequest objectResignRequest = PutObjectPresignRequest.builder().signatureDuration(EXPIRY_TIME).putObjectRequest(getObjectRequest).build();
        URL url = s3Presigner.presignPutObject(objectResignRequest).url();
        s3Presigner.close();
        return url.toString();
    }

    public String getGroupProfilePicUrl(UUID picId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(TEAM_PATH + picId).build();
        GetObjectPresignRequest objectPreSignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(EXPIRY_TIME).build();
        URL url = s3Presigner.presignGetObject(objectPreSignRequest).url();
        s3Presigner.close();
        return url.toString();
    }

    public List<String> getTeamPostPicUrl(List<UUID> picIds) {
        ArrayList<String> s3Urls = new ArrayList<>();
        for (UUID picId: picIds) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(TEAM_POST_PATH + picId).build();
            GetObjectPresignRequest objectPreSignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(EXPIRY_TIME).build();
            URL url = s3Presigner.presignGetObject(objectPreSignRequest).url();
            s3Urls.add(url.toString());
            s3Presigner.close();
        }
        return s3Urls;
    }

    public List<String> setTeamPostPreSignedUrl(int imgCnt){
        return IntStream.range(0, imgCnt)
                .mapToObj(i -> {
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(TEAM_POST_PATH + UUID.randomUUID())
                            .build();

                    PutObjectPresignRequest objectPresignRequest = PutObjectPresignRequest.builder()
                            .putObjectRequest(putObjectRequest)
                            .signatureDuration(EXPIRY_TIME)
                            .build();

                    return s3Presigner.presignPutObject(objectPresignRequest).url().toString();
                }).toList();
    }

    public void verifyImgsExistInTeamPostS3(List<UUID> imgIds) {
        for (UUID imgId : imgIds) {
            verifyImgExist(TEAM_POST_PATH, imgId);
        }
    }

    private void verifyImgExist(String keyPath, UUID imgId) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(keyPath + imgId)
                .build();

        s3Client.headObject(headObjectRequest);
        s3Client.close();
    }

    public String getTeamMemberPreSignedUrl(UUID imgId){
        try {
            verifyImgExist(TEAM_MEMBER_PATH, imgId);
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(TEAM_MEMBER_PATH + imgId).build();
            GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(EXPIRY_TIME).build();
            String url = s3Presigner.presignGetObject(preSignRequest).url().toString();
            s3Client.close();

            return url;
        }catch (S3Exception e){
            return getMemberProfilePicUrl(imgId);
        }
    }

    public String setTeamMemberProfilePreSignedUrl(UUID teamMemberProfileUUID) {
        PutObjectRequest getObjectRequest = PutObjectRequest.builder().bucket(bucket).key(TEAM_MEMBER_PATH + teamMemberProfileUUID).build();
        PutObjectPresignRequest objectResignRequest = PutObjectPresignRequest.builder().signatureDuration(EXPIRY_TIME).putObjectRequest(getObjectRequest).build();
        URL url = s3Presigner.presignPutObject(objectResignRequest).url();
        s3Presigner.close();
        return url.toString();
    }

    public String getTeamBackgroundPreSignedUrl(UUID teamBackgroundUUID) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(TEAM_BACKGROUND_PATH + teamBackgroundUUID).build();
        GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(EXPIRY_TIME).build();
        URL url = s3Presigner.presignGetObject(preSignRequest).url();
        s3Client.close();

        return url.toString();
    }

    public String setTeamBackgroundPreSignedUrl(UUID teamBackgroundUUID) {
        PutObjectRequest getObjectRequest = PutObjectRequest.builder().bucket(bucket).key(TEAM_BACKGROUND_PATH + teamBackgroundUUID).build();
        PutObjectPresignRequest objectResignRequest = PutObjectPresignRequest.builder().signatureDuration(EXPIRY_TIME).putObjectRequest(getObjectRequest).build();
        URL url = s3Presigner.presignPutObject(objectResignRequest).url();
        s3Presigner.close();
        return url.toString();
    }
}

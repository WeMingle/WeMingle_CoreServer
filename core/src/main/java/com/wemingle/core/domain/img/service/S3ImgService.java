package com.wemingle.core.domain.img.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImgService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Presigner s3Presigner;

    public String getMemberProfilePicUrl(UUID picId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key("profile/member/" + picId).build();
        GetObjectPresignRequest objectPreSignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(Duration.ofMinutes(1)).build();
        URL url = s3Presigner.presignGetObject(objectPreSignRequest).url();
        s3Presigner.close();
        return url.toString();
    }
    public String setMemberProfilePreSignedUrl(UUID memberProfileUUID) {
        PutObjectRequest getObjectRequest = PutObjectRequest.builder().bucket(bucket).key("profile/member/"+ memberProfileUUID).build();
        PutObjectPresignRequest objectPreSignRequest = PutObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(1)).putObjectRequest(getObjectRequest).build();
        URL url = s3Presigner.presignPutObject(objectPreSignRequest).url();
        s3Presigner.close();
        return url.toString();
    }

    public String setGroupProfilePreSignedUrl(UUID groupProfileUUID) {
        PutObjectRequest getObjectRequest = PutObjectRequest.builder().bucket(bucket).key("profile/group/"+groupProfileUUID).build();
        PutObjectPresignRequest objectResignRequest = PutObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(1)).putObjectRequest(getObjectRequest).build();
        URL url = s3Presigner.presignPutObject(objectResignRequest).url();
        s3Presigner.close();
        return url.toString();
    }

    public String getGroupProfilePicUrl(UUID picId) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key("profile/group/" + picId).build();
        GetObjectPresignRequest objectPreSignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(Duration.ofMinutes(1)).build();
        URL url = s3Presigner.presignGetObject(objectPreSignRequest).url();
        s3Presigner.close();
        return url.toString();
    }

    public List<String> getTeamPostPicUrl(List<UUID> picIds) {
        ArrayList<String> s3Urls = new ArrayList<>();
        for (UUID picId: picIds) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key("post/team/" + picId).build();
            GetObjectPresignRequest objectPreSignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(Duration.ofMinutes(1)).build();
            URL url = s3Presigner.presignGetObject(objectPreSignRequest).url();
            s3Urls.add(url.toString());
            s3Presigner.close();
        }
        return s3Urls;
    }

    public List<String> setTeamPostPreSignedUrl(int imgCnt){
        ArrayList<String> s3Urls = new ArrayList<>();
        for (int i = 0; i < imgCnt; i++) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key("post/team/" + UUID.randomUUID()).build();
            PutObjectPresignRequest objectPresignRequest = PutObjectPresignRequest.builder().putObjectRequest(putObjectRequest).signatureDuration(Duration.ofMinutes(1)).build();
            URL url = s3Presigner.presignPutObject(objectPresignRequest).url();
            s3Urls.add(url.toString());
            s3Presigner.close();
        }
        return s3Urls;
    }
}

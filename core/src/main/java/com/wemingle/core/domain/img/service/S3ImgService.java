package com.wemingle.core.domain.img.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImgService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    private static final String TEAM_POST_PATH = "post/team/";

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
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(TEAM_POST_PATH + picId).build();
            GetObjectPresignRequest objectPreSignRequest = GetObjectPresignRequest.builder().getObjectRequest(getObjectRequest).signatureDuration(Duration.ofMinutes(1)).build();
            URL url = s3Presigner.presignGetObject(objectPreSignRequest).url();
            s3Urls.add(url.toString());
            s3Presigner.close();
        }
        return s3Urls;
    }

    public HashMap<String, ArrayList<String>> setTeamPostPreSignedUrl(List<String> extensions){
        HashMap<String, ArrayList<String>> teamPostPreSignedUrls = new HashMap<>();
        extensions.forEach(extension -> {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(TEAM_POST_PATH + UUID.randomUUID() + "." + extension).build();
            PutObjectPresignRequest objectPresignRequest = PutObjectPresignRequest.builder().putObjectRequest(putObjectRequest).signatureDuration(Duration.ofMinutes(1)).build();
            URL url = s3Presigner.presignPutObject(objectPresignRequest).url();
            putValue(teamPostPreSignedUrls, extension, url.toString());
            s3Presigner.close();
        });

        return teamPostPreSignedUrls;
    }

    private void putValue(HashMap<String, ArrayList<String>> teamPostPreSignedUrls, String key, String value){
        try {
            ArrayList<String> valueList = teamPostPreSignedUrls.get(key);
            valueList.add(value);
            teamPostPreSignedUrls.put(key, valueList);
        }catch (RuntimeException e){
            ArrayList<String> startList = new ArrayList<>();
            startList.add(value);
            teamPostPreSignedUrls.put(key, startList);
        }
    }

    public void verifyImgExist(List<UUID> imgIds) {
        for (UUID imgId : imgIds) {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(TEAM_POST_PATH + imgId.toString())
                    .build();

            s3Client.headObject(headObjectRequest);
            s3Client.close();
        }
    }
}

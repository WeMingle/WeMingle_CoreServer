package com.wemingle.core.domain.img.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImgService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Presigner s3Presigner;
    public String getMemberProfilePreSignedUrl(String memberProfileUUID) {
        PutObjectRequest getObjectRequest = PutObjectRequest.builder().key("profile/member/"+ memberProfileUUID).bucket(bucket).build();
        PutObjectPresignRequest objectResignRequest = PutObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(1)).putObjectRequest(getObjectRequest).build();
        URL url = s3Presigner.presignPutObject(objectResignRequest).url();
        s3Presigner.close();
        return url.toString();
    }

    public String getGroupProfilePreSignedUrl() {
        PutObjectRequest getObjectRequest = PutObjectRequest.builder().key("profile/group/"+UUID.randomUUID()).bucket(bucket).build();
        PutObjectPresignRequest objectResignRequest = PutObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(1)).putObjectRequest(getObjectRequest).build();
        URL url = s3Presigner.presignPutObject(objectResignRequest).url();
        s3Presigner.close();
        return url.toString();
    }
}

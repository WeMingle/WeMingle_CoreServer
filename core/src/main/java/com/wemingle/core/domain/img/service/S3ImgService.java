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
}

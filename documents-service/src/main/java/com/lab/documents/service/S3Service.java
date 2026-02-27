package com.lab.documents.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucket;

    public byte[] downloadFile(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
        try {
            return response.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to download: " + key, e);
        }
    }

    public String uploadFile(String key, byte[] data, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build();
        s3Client.putObject(request, RequestBody.fromBytes(data));
        return key;
    }
}
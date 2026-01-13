package com.example.policy.storage.impl;

import com.example.policy.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;

/**
 * AWS S3 file storage service / AWS S3 文件存储服务
 * For production environment / 用于生产环境
 * 
 * Note: Requires AWS SDK dependency / 需要 AWS SDK 依赖
 * Add to pom.xml:
 * <dependency>
 *     <groupId>software.amazon.awssdk</groupId>
 *     <artifactId>s3</artifactId>
 * </dependency>
 */
@Service
@Profile("prod")
public class S3FileStorageService implements FileStorageService {
    
    @Value("${storage.s3.bucket:complog-bucket}")
    private String bucket;
    
    @Value("${storage.s3.region:us-east-1}")
    private String region;
    
    private final S3Client s3Client;
    private final S3Presigner presigner;
    
    public S3FileStorageService() {
        // Initialize S3 client / 初始化 S3 客户端
        this.s3Client = S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.of(region))
                .build();
        this.presigner = S3Presigner.create();
    }
    
    @Override
    public String upload(String key, InputStream inputStream, String contentType) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();
            
            s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));
            
            // Return S3 URL / 返回 S3 URL
            return "s3://" + bucket + "/" + key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3 / 上传文件到 S3 失败: " + key, e);
        }
    }
    
    @Override
    public String getUrl(String key) {
        // Generate presigned URL (valid for 1 hour) / 生成预签名URL（1小时有效）
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(r -> r.bucket(bucket).key(key))
                .build();
        
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }
    
    @Override
    public InputStream download(String key) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            
            return s3Client.getObjectAsBytes(getRequest).asInputStream();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3 / 从 S3 下载文件失败: " + key, e);
        }
    }
    
    @Override
    public boolean delete(String key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            
            s3Client.deleteObject(deleteRequest);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3 / 从 S3 删除文件失败: " + key, e);
        }
    }
    
    @Override
    public boolean exists(String key) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            
            s3Client.headObject(headRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to check file existence in S3 / 检查 S3 文件是否存在失败: " + key, e);
        }
    }
}
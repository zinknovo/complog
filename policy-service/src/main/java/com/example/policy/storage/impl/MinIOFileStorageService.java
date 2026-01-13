package com.example.policy.storage.impl;

import com.example.policy.storage.FileStorageService;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * MinIO file storage service / MinIO 文件存储服务
 * For test environment / 用于测试环境
 * MinIO is S3-compatible object storage / MinIO 是 S3 兼容的对象存储
 * 
 * Note: Requires MinIO dependency / 需要 MinIO 依赖
 * Add to pom.xml:
 * <dependency>
 *     <groupId>io.minio</groupId>
 *     <artifactId>minio</artifactId>
 *     <version>8.5.0</version>
 * </dependency>
 */
@Service
@Profile("test")
public class MinIOFileStorageService implements FileStorageService {
    
    @Value("${storage.minio.endpoint:http://localhost:9000}")
    private String endpoint;
    
    @Value("${storage.minio.bucket:complog-bucket}")
    private String bucket;
    
    @Value("${storage.minio.access-key:minioadmin}")
    private String accessKey;
    
    @Value("${storage.minio.secret-key:minioadmin}")
    private String secretKey;
    
    private MinioClient minioClient;
    
    public MinIOFileStorageService() {
        // Initialize MinIO client / 初始化 MinIO 客户端
        // Will be initialized in @PostConstruct or lazy initialization
        // 将在 @PostConstruct 或懒加载时初始化
    }
    
    private MinioClient getClient() {
        if (minioClient == null) {
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            
            // Create bucket if not exists / 如果桶不存在则创建
            try {
                boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
                if (!found) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize MinIO bucket / 初始化 MinIO 桶失败", e);
            }
        }
        return minioClient;
    }
    
    @Override
    public String upload(String key, InputStream inputStream, String contentType) {
        try {
            getClient().putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build()
            );
            
            // Return MinIO URL / 返回 MinIO URL
            return endpoint + "/" + bucket + "/" + key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO / 上传文件到 MinIO 失败: " + key, e);
        }
    }
    
    @Override
    public String getUrl(String key) {
        try {
            // Generate presigned URL (valid for 1 hour) / 生成预签名URL（1小时有效）
            return getClient().getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.GET)
                            .bucket(bucket)
                            .object(key)
                            .expiry(60 * 60) // 1 hour / 1小时
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get MinIO URL / 获取 MinIO URL 失败: " + key, e);
        }
    }
    
    @Override
    public InputStream download(String key) {
        try {
            return getClient().getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from MinIO / 从 MinIO 下载文件失败: " + key, e);
        }
    }
    
    @Override
    public boolean delete(String key) {
        try {
            getClient().removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from MinIO / 从 MinIO 删除文件失败: " + key, e);
        }
    }
    
    @Override
    public boolean exists(String key) {
        try {
            getClient().statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new RuntimeException("Failed to check file existence in MinIO / 检查 MinIO 文件是否存在失败: " + key, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check file existence in MinIO / 检查 MinIO 文件是否存在失败: " + key, e);
        }
    }
}
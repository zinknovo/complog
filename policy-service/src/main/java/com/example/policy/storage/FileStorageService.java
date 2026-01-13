package com.example.policy.storage;

import java.io.InputStream;

/**
 * File storage service interface / 文件存储服务接口
 * Abstract storage layer for local/S3/MinIO / 本地/S3/MinIO 的抽象存储层
 */
public interface FileStorageService {
    
    /**
     * Upload file to storage / 上传文件到存储
     * @param key File key (path) / 文件键（路径）
     * @param inputStream File input stream / 文件输入流
     * @param contentType Content type (e.g., "text/html", "application/pdf") / 内容类型
     * @return File URL / 文件URL
     */
    String upload(String key, InputStream inputStream, String contentType);
    
    /**
     * Get file URL / 获取文件URL
     * @param key File key / 文件键
     * @return File URL / 文件URL
     */
    String getUrl(String key);
    
    /**
     * Download file / 下载文件
     * @param key File key / 文件键
     * @return File input stream / 文件输入流
     */
    InputStream download(String key);
    
    /**
     * Delete file / 删除文件
     * @param key File key / 文件键
     * @return Success or not / 是否成功
     */
    boolean delete(String key);
    
    /**
     * Check if file exists / 检查文件是否存在
     * @param key File key / 文件键
     * @return Exists or not / 是否存在
     */
    boolean exists(String key);
}
package com.example.policy.storage.impl;

import com.example.policy.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Local file storage service / 本地文件存储服务
 * For development environment / 用于开发环境
 */
@Service
@Profile("local")
public class LocalFileStorageService implements FileStorageService {
    
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    @Override
    public String upload(String key, InputStream inputStream, String contentType) {
        try {
            String filePath = uploadPath + "/" + key;
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            // Write file / 写入文件
            try (FileOutputStream fos = new FileOutputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(inputStream)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            
            // Return local URL / 返回本地URL
            return "http://localhost:" + serverPort + "/files/" + key;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file / 上传文件失败: " + key, e);
        }
    }
    
    @Override
    public String getUrl(String key) {
        return "http://localhost:" + serverPort + "/files/" + key;
    }
    
    @Override
    public InputStream download(String key) {
        try {
            String filePath = uploadPath + "/" + key;
            return Files.newInputStream(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file / 下载文件失败: " + key, e);
        }
    }
    
    @Override
    public boolean delete(String key) {
        try {
            String filePath = uploadPath + "/" + key;
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file / 删除文件失败: " + key, e);
        }
    }
    
    @Override
    public boolean exists(String key) {
        String filePath = uploadPath + "/" + key;
        return new File(filePath).exists();
    }
}
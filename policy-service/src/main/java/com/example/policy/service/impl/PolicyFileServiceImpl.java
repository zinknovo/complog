package com.example.policy.service.impl;

import com.example.policy.service.PolicyFileService;
import com.example.policy.storage.FileStorageService;
import com.example.policy.tenant.context.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Policy file service implementation / 制度文件服务实现
 * Uses FileStorageService for abstraction / 使用 FileStorageService 进行抽象
 */
@Service
public class PolicyFileServiceImpl implements PolicyFileService {
    
    @Autowired(required = false)
    private FileStorageService fileStorageService;
    
    @Value("${file.upload.path:/uploads}")
    private String uploadPath;
    
    /**
     * Generate version file key / 生成版本文件键
     * Format: policies/{tenant_id}/{policy_id}/{version}/content.html
     */
    private String generateVersionFileKey(Long policyId, String version) {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            tenantId = "default";
        }
        return String.format("policies/%s/%d/%s/content.html", tenantId, policyId, version);
    }
    
    @Override
    public String generateVersionFilePath(Long policyId, String version) {
        // For backward compatibility, return key / 为了向后兼容，返回键
        // In new implementation, this will be replaced by URL / 在新实现中，这将被 URL 替代
        if (fileStorageService != null) {
            return generateVersionFileKey(policyId, version);
        }
        return String.format("%s/policies/%d/%s/content.html", uploadPath, policyId, version);
    }
    
    @Override
    public String saveVersionContent(Long policyId, String version, String content) {
        // If FileStorageService is available, use it / 如果 FileStorageService 可用，使用它
        if (fileStorageService != null) {
            String key = generateVersionFileKey(policyId, version);
            InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            return fileStorageService.upload(key, inputStream, "text/html; charset=utf-8");
        }
        
        // Fallback to local file system (legacy) / 回退到本地文件系统（旧版）
        return saveVersionContentLocal(policyId, version, content);
    }
    
    /**
     * Legacy local file save / 旧版本地文件保存
     */
    private String saveVersionContentLocal(Long policyId, String version, String content) {
        String filePath = String.format("%s/policies/%d/%s/content.html", uploadPath, policyId, version);
        java.io.File file = new java.io.File(filePath);
        file.getParentFile().mkdirs();
        
        try (java.io.FileWriter writer = new java.io.FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(content);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to save version file / 保存版本文件失败", e);
        }
        
        return filePath;
    }
    
    @Override
    public String readVersionContent(String filePathOrUrl) {
        // If FileStorageService is available, try to use it / 如果 FileStorageService 可用，尝试使用它
        if (fileStorageService != null) {
            // Check if it's a key (not a full path) / 检查是否是键（不是完整路径）
            if (!filePathOrUrl.startsWith("/") && !filePathOrUrl.startsWith("http") && !filePathOrUrl.startsWith("s3://")) {
                // It's a key, use storage service / 是键，使用存储服务
                try (InputStream inputStream = fileStorageService.download(filePathOrUrl)) {
                    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    // Fallback to local / 回退到本地
                }
            } else if (fileStorageService.exists(filePathOrUrl)) {
                // It's a key that exists in storage / 是存储中存在的键
                try (InputStream inputStream = fileStorageService.download(filePathOrUrl)) {
                    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    // Fallback to local / 回退到本地
                }
            }
        }
        
        // Fallback to local file system (legacy) / 回退到本地文件系统（旧版）
        try {
            return Files.readString(Paths.get(filePathOrUrl), StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read version file / 读取版本文件失败", e);
        }
    }
    
    @Override
    public boolean deleteVersionFiles(Long policyId, String version) {
        String key = generateVersionFileKey(policyId, version);
        
        // If FileStorageService is available, use it / 如果 FileStorageService 可用，使用它
        if (fileStorageService != null) {
            return fileStorageService.delete(key);
        }
        
        // Fallback to local file system (legacy) / 回退到本地文件系统（旧版）
        String dirPath = String.format("%s/policies/%d/%s", uploadPath, policyId, version);
        java.io.File dir = new java.io.File(dirPath);
        
        if (dir.exists() && dir.isDirectory()) {
            // Archive instead of delete (soft delete) / 归档而不是删除（软删除）
            try {
                String archivePath = String.format("%s/archive/policies/%d/%s", uploadPath, policyId, version);
                java.io.File archiveDir = new java.io.File(archivePath);
                archiveDir.getParentFile().mkdirs();
                
                // Move directory / 移动目录
                dir.renameTo(archiveDir);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
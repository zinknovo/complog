package com.example.complog.service;

/**
 * Policy file service / 制度文件服务
 * Handle file storage and reading for policy content / 处理制度内容的文件存储和读取
 */
public interface PolicyFileService {
    /**
     * Generate version file path / 生成版本文件路径
     * @param policyId Policy ID / 制度ID
     * @param version Version number / 版本号
     * @return File path / 文件路径
     */
    String generateVersionFilePath(Long policyId, String version);

    /**
     * Save version content to file / 保存版本内容到文件
     * @param policyId Policy ID / 制度ID
     * @param version Version number / 版本号
     * @param content Content to save / 要保存的内容
     * @return File path / 文件路径
     */
    String saveVersionContent(Long policyId, String version, String content);

    /**
     * Read version content from file / 从文件读取版本内容
     * @param filePath File path / 文件路径
     * @return Content / 内容
     */
    String readVersionContent(String filePath);

    /**
     * Delete version files (soft delete, can archive) / 删除版本文件（软删除，实际可以归档）
     * @param policyId Policy ID / 制度ID
     * @param version Version number / 版本号
     * @return Success or not / 是否成功
     */
    boolean deleteVersionFiles(Long policyId, String version);
}
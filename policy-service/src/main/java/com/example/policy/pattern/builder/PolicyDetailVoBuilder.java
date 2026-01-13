package com.example.complog.pattern.builder;

import com.example.policy.domain.Department;
import com.example.policy.domain.Policy;
import com.example.policy.service.PolicyFileService;
import com.example.policy.vo.PolicyDetailVo;

/**
 * Policy detail VO builder / 制度详情VO建造者
 * Builder Pattern - Builder class for complex VO construction / 建造者模式 - 复杂VO构建类
 */
public class PolicyDetailVoBuilder {
    
    private PolicyDetailVo vo;
    
    /**
     * Private constructor / 私有构造函数
     */
    private PolicyDetailVoBuilder() {
        this.vo = new PolicyDetailVo();
    }
    
    /**
     * Create builder / 创建建造者
     * @return Builder / 建造者
     */
    public static PolicyDetailVoBuilder create() {
        return new PolicyDetailVoBuilder();
    }
    
    /**
     * Set basic policy information / 设置基本制度信息
     * @param policy Policy / 制度
     * @return Builder / 建造者
     */
    public PolicyDetailVoBuilder withPolicy(Policy policy) {
        if (policy != null) {
            vo.setId(policy.getId());
            vo.setName(policy.getName());
            vo.setCode(policy.getCode());
            vo.setType(policy.getType());
            vo.setCurrentVersion(policy.getCurrentVersion());
            vo.setCurrentRevisionId(policy.getCurrentRevisionId());
            vo.setContentSummary(policy.getContentSummary());
            vo.setContentFilePath(policy.getContentFilePath());
            vo.setEffectiveDate(policy.getEffectiveDate());
            vo.setExpiryDate(policy.getExpiryDate());
            vo.setStatus(policy.getStatus());
            vo.setOwnerDeptId(policy.getOwnerDeptId());
            vo.setCreator(policy.getCreator());
            vo.setCreatedAt(policy.getCreatedAt());
            vo.setUpdatedAt(policy.getUpdatedAt());
            vo.setUpdater(policy.getUpdater());
        }
        return this;
    }
    
    /**
     * Set type text / 设置类型文本
     * @param typeText Type text / 类型文本
     * @return Builder / 建造者
     */
    public PolicyDetailVoBuilder withTypeText(String typeText) {
        vo.setTypeText(typeText);
        return this;
    }
    
    /**
     * Set status text / 设置状态文本
     * @param statusText Status text / 状态文本
     * @return Builder / 建造者
     */
    public PolicyDetailVoBuilder withStatusText(String statusText) {
        vo.setStatusText(statusText);
        return this;
    }
    
    /**
     * Set owner department / 设置负责部门
     * @param department Department / 部门
     * @return Builder / 建造者
     */
    public PolicyDetailVoBuilder withOwnerDepartment(Department department) {
        if (department != null) {
            vo.setOwnerDeptName(department.getName());
        }
        return this;
    }
    
    /**
     * Set content from file service / 从文件服务设置内容
     * @param policyFileService File service / 文件服务
     * @return Builder / 建造者
     */
    public PolicyDetailVoBuilder withContentFromFile(PolicyFileService policyFileService) {
        if (vo.getContentFilePath() != null && !vo.getContentFilePath().isEmpty()) {
            try {
                vo.setContent(policyFileService.readVersionContent(vo.getContentFilePath()));
            } catch (Exception e) {
                // If file read fails, use summary / 如果文件读取失败，使用摘要
                vo.setContent(vo.getContentSummary());
            }
        } else {
            vo.setContent(vo.getContentSummary());
        }
        return this;
    }
    
    /**
     * Set content directly / 直接设置内容
     * @param content Content / 内容
     * @return Builder / 建造者
     */
    public PolicyDetailVoBuilder withContent(String content) {
        vo.setContent(content);
        return this;
    }
    
    /**
     * Build VO / 构建VO
     * @return PolicyDetailVo / 制度详情VO
     */
    public PolicyDetailVo build() {
        return vo;
    }
}

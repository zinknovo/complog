package com.example.policy.vo;

import lombok.Data;
import java.util.Date;

/**
 * Policy detail VO / 制度详情VO
 */
@Data
public class PolicyDetailVo {
    private Long id;
    private String name;
    private String code;
    private Integer type;
    private String typeText;
    private String currentVersion;
    private Long currentRevisionId;
    private String contentSummary;
    private String content;  // Full content / 完整内容
    private String contentFilePath;
    private Date effectiveDate;
    private Date expiryDate;
    private Integer status;
    private String statusText;
    private Long ownerDeptId;
    private String ownerDeptName;
    private String creator;
    private Date createdAt;
    private Date updatedAt;
    private String updater;
}
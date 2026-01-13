package com.example.policy.vo;

import lombok.Data;
import java.util.List;

/**
 * Policy revision add VO / 创建修订VO
 */
@Data
public class PolicyRevisionAddVo {
    private Long policyId;
    private String baseVersion;  // Base version to create from / 基于哪个版本创建
    private String revisionType;  // major/minor/patch / 修订类型
    private String title;
    private String content;
    private String revisionReason;
    private Long initiatorId;
    private Long initiatorDeptId;
    
    // Department IDs for review / 参与审议的部门ID列表
    private List<Long> deptIds;  // Department IDs / 部门ID列表
    private Integer isRequired;  // Is required review for all departments: 1-required, 0-optional / 所有部门是否必审：1-必须，0-可选
}
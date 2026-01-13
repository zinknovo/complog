package com.example.policy.vo;

import lombok.Data;
import java.util.Date;

/**
 * Policy list VO / 制度列表VO
 */
@Data
public class PolicyListVo {
    private Long id;
    private String name;
    private String code;
    private Integer type;
    private String typeText;  // Type description / 类型描述
    private String currentVersion;
    private Integer status;
    private String statusText;  // Status description / 状态描述
    private Long ownerDeptId;
    private String ownerDeptName;  // Owner department name / 负责部门名称
    private Date effectiveDate;
    private Date expiryDate;
    private String creator;
    private Date createdAt;
    private Date updatedAt;
}
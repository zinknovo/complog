package com.example.policy.vo;

import lombok.Data;
import java.util.Date;

/**
 * Policy revision list VO / 修订列表VO
 */
@Data
public class PolicyRevisionListVo {
    private Long id;
    private Long policyId;
    private String policyName;  // Policy name / 制度名称
    private String version;
    private String title;
    private String revisionReason;
    
    // Status information (friendly display) / 友好状态展示
    private Integer status;
    private String statusText;  // "Draft", "Pending Review", "Reviewing", etc. / "草稿"、"待审议"、"审议中"等
    private String statusColor;  // Color identifier / 颜色标识：gray, blue, orange, green, red
    
    // Progress information / 进度信息
    private Integer progress;  // Progress percentage: 0-100 / 进度百分比：0-100
    private String progressText;  // "3/5 departments completed review" / "3/5 部门已完成审议"
    
    private String initiatorName;  // Initiator name / 发起人姓名
    private String initiatorDeptName;  // Initiator department name / 发起部门名称
    private Date createdAt;
    private Date updatedAt;
}
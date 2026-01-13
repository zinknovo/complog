package com.example.policy.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * Policy revision detail VO / 修订详情VO
 */
@Data
public class PolicyRevisionDetailVo {
    private Long id;
    private Long policyId;
    private String policyName;
    private String policyCode;
    private String version;
    private String title;
    private String content;  // Revision content (rich text) / 修订内容（富文本）
    private String revisionReason;
    
    // Status information (friendly display) / 状态信息（友好展示）
    private Integer status;
    private String statusText;
    private String statusColor;
    private String statusDescription;  // Status description / 状态说明："Currently 3 departments are reviewing"
    
    // Progress details / 进度详情
    private RevisionProgressVo progress;  // Progress detail object / 进度详情对象
    
    // Initiator information / 发起信息
    private Long initiatorId;
    private String initiatorName;
    private Long initiatorDeptId;
    private String initiatorDeptName;
    
    private Date createdAt;
    private Date updatedAt;
    
    // Available action buttons hint / 可操作按钮提示
    private List<String> availableActions;  // ["Submit for Review", "Edit Content", "View Progress"]
}
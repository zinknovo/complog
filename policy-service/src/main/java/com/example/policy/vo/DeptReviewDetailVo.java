package com.example.policy.vo;

import lombok.Data;
import java.util.Date;

/**
 * Department review detail VO / 部门审议详情VO
 */
@Data
public class DeptReviewDetailVo {
    private Long reviewId;
    private Long deptId;
    private String deptName;
    private Boolean isRequired;  // Is required review / 是否必审
    
    // Review status (friendly display) / 审议状态（友好展示）
    private Integer opinion;  // 0-pending, 1-agree, 2-disagree, 3-need modification
    private String opinionText;  // "Pending", "Agree", "Disagree", "Need Modification" / "待审议"、"同意"、"不同意"、"需修改"
    private String opinionColor;  // Color identifier / 颜色标识
    
    private String reviewComment;  // Review comment content / 审议意见内容
    private Long reviewerId;
    private String reviewerName;  // Reviewer name / 审议人姓名
    private Date reviewTime;
    
    // Status icon hint / 状态图标提示
    private String statusIcon;  // "clock", "check", "close", "edit"
}
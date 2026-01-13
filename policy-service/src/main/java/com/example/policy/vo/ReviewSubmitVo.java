package com.example.policy.vo;

import lombok.Data;

/**
 * Review submit VO / 提交审议意见VO
 */
@Data
public class ReviewSubmitVo {
    private Long deptId;  // Review department ID / 审议部门ID
    private Long reviewerId;  // Reviewer user ID / 审议人ID
    private Integer opinion;  // 1-agree, 2-disagree, 3-need modification / 1-同意，2-不同意，3-需修改
    private String comment;  // Review comment / 审议意见
}
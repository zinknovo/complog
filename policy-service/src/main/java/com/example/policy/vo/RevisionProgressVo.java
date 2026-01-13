package com.example.policy.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * Revision progress VO - Core visualization data / 审议进度VO - 核心可视化数据（最重要！）
 */
@Data
public class RevisionProgressVo {
    // Revision basic information / 修订基本信息
    private Long revisionId;
    private Integer revisionStatus;  // Revision status code / 修订状态码
    private String revisionStatusText;  // "Draft", "Pending Review", "Reviewing", "Approved", "Rejected", "Effective"
    private String revisionStatusColor;  // Color identifier / 颜色标识
    
    // Progress statistics (key data) / 进度统计（关键数据）
    private Integer totalDepts;  // Total participating departments / 总参与部门数
    private Integer requiredDepts;  // Required review departments / 必审部门数
    private Integer reviewedCount;  // Reviewed departments count / 已审议部门数
    private Integer pendingCount;  // Pending review departments count / 待审议部门数
    private Integer agreedCount;  // Agreed departments count / 同意部门数
    private Integer rejectedCount;  // Disagreed departments count / 不同意部门数
    private Integer needModifyCount;  // Need modification departments count / 需修改部门数
    
    // Progress percentage / 进度百分比
    private Integer progressPercent;  // Overall progress: 0-100 / 整体进度：0-100
    private String progressText;  // "3/5 departments completed review, 2 agreed, 1 need modification" / "3/5 部门已完成审议，其中2个同意，1个需修改"
    
    // Time information / 时间信息
    private Date submittedAt;  // Submitted for review time / 提交审议时间
    private Date lastReviewAt;  // Last review time / 最后审议时间
    private String elapsedDays;  // Elapsed time / 已耗时："已审议3天"
    
    // Department review details (for list display) / 各部门审议详情（用于列表展示）
    private List<DeptReviewDetailVo> deptReviews;
    
    // Process timeline (optional, for visualization timeline) / 流程时间线（可选，用于可视化时间轴）
    private List<ProcessTimelineVo> timeline;
    
    // Next step operation hint / 下一步操作提示
    private String nextStepHint;  // "Waiting for Procurement and Finance departments to complete review"
    private Boolean canMakeEffective;  // Can execute make-effective operation / 是否可以执行生效操作
    private Boolean canEdit;  // Can edit / 是否可以编辑
}
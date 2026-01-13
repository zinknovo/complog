package com.example.policy.vo;

import lombok.Data;
import java.util.Date;

/**
 * Policy version history VO / 版本历史VO
 */
@Data
public class PolicyVersionHistoryVo {
    private Long revisionId;
    private String version;  // v1.0.0
    private String title;  // Revision title / 修订标题
    private Integer status;
    private String statusText;  // "Effective", "Approved", "Rejected" / "已生效"、"已通过"、"已驳回"
    private Boolean isCurrent;  // Is current effective version / 是否为当前生效版本
    private Date effectiveAt;  // Effective time / 生效时间
    private Date createdAt;  // Created time / 创建时间
    private String initiatorName;  // Initiator / 发起人
    private String revisionReason;  // Revision reason (summary) / 修订原因（摘要）
}
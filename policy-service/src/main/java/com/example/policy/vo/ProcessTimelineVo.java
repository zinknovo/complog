package com.example.policy.vo;

import lombok.Data;
import java.util.Date;

/**
 * Process timeline VO (optional, for visualization) / 流程时间线VO（可选，用于可视化）
 */
@Data
public class ProcessTimelineVo {
    private String event;  // "Submit for Review", "Legal Department Review", "Procurement Department Review" / "提交审议"、"法务部门审议"、"采购部门审议"
    private Date time;
    private String actor;  // Operator/person/department / 操作人/部门
    private String description;  // Operation description / 操作描述
    private String status;  // "success", "pending", "reject"
}
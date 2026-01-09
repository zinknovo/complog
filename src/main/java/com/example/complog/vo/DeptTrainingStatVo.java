package com.example.complog.vo;

import lombok.Data;

/**
 * Department training statistics view.
 */
@Data
public class DeptTrainingStatVo {
    private Long deptId;
    private String deptName;
    private Long totalJoin;
    private Long completedCount;
    private Double completionRate;
}

package com.example.complog.vo;

import lombok.Data;

/**
 * Activity statistics view.
 */
@Data
public class ActivityStatVo {
    private Long activityId;
    private String activityName;
    private Long totalJoin;
    private Long completedCount;
    private Double completionRate;
}

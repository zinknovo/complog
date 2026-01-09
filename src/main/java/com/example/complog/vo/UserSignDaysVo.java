package com.example.complog.vo;

import com.example.complog.domain.Task;
import com.example.complog.domain.UserTaskRecord;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: Xintao Hu
 * @Desctription: TODO
 * @Date: Modified on 2026/1/8 15:37
 * @Version: 1.0
 */
@Data
public class UserSignDaysVo {

    //累计打卡天数
    private Integer totalDays;
    /**
     * 活动开始时间
     */
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;

    //userId+活动id，1 从现在开始到明天晚上23:59,59

    /**
     * 连续打卡天数
     */
    private Integer continuousDays;

    /**
     * 打卡列表
     */
    //根据用户id，活动id，单表查询UserTaskRecord
    private List<UserTaskRecord> taskRecords;

    /**
     * 今天的任务
     */
    private Task nextTask;


    //查询所有的，减去已经做的 = 还需要做的任务，去第一个
}

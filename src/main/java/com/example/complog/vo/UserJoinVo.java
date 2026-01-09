package com.example.complog.vo;

import lombok.Data;

import java.util.Date;

/**
 * User join list view.
 */
@Data
public class UserJoinVo {

    private String activityName;

    private Integer status;

    private String userName;

    private String deptName;

    private Date startTime;

    private Date endTime;


}

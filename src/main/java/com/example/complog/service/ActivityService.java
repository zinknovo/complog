package com.example.complog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.complog.domain.Activity;
import com.example.complog.response.PageResult;
import com.example.complog.vo.ActivityAddVo;
import com.example.complog.vo.ActivityDetailVo;
import com.example.complog.vo.ActivityEditVo;
import com.example.complog.vo.ActivityStatVo;
import com.example.complog.vo.DeptTrainingStatVo;
import com.example.complog.vo.TaskListVo;
import com.example.complog.vo.UserJoinVo;
import com.example.complog.vo.UserTaskListVo;
import java.util.List;

/**
* @author Z1nk
* @description 针对表【activity】的数据库操作Service
* @createDate 2026-01-08 16:10:57
*/
public interface ActivityService extends IService<Activity> {
    boolean add(ActivityAddVo activityAddVo);

    boolean edit(ActivityEditVo activityEditVo);

    boolean del(Long id);

    PageResult<ActivityDetailVo> listDetail(Integer pageNum, Integer pageSize, String activityName, Integer status);

    PageResult<UserTaskListVo> getUserTask(Integer pageNum, Integer pageSize);

    PageResult<UserJoinVo> getUserJoin(Integer pageNum, Integer pageSize);

    PageResult<ActivityStatVo> getActivityStat(Integer pageNum, Integer pageSize, String activityName);

    PageResult<DeptTrainingStatVo> getDeptStat(Integer pageNum, Integer pageSize, Long activityId);

    List<TaskListVo> listTasks(Long activityId);

    boolean addTask(Long activityId, Long taskId);

    Long cloneTask(Long activityId, Long taskId);

    boolean removeTask(Long activityId, Long taskId);

}

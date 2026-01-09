package com.example.complog.controller.background;

import com.example.complog.response.AjaxResult;
import com.example.complog.response.enums.ErrorEnum;
import com.example.complog.response.PageResult;
import com.example.complog.service.ActivityService;
import com.example.complog.vo.ActivityAddVo;
import com.example.complog.vo.ActivityDetailVo;
import com.example.complog.vo.ActivityEditVo;
import com.example.complog.vo.ActivityStatVo;
import com.example.complog.vo.DeptTrainingStatVo;
import com.example.complog.vo.TaskListVo;
import com.example.complog.vo.UserJoinVo;
import com.example.complog.vo.UserTaskListVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Xintao Hu
 * @Desctription: TODO
 * @Date: Modified on 2026/1/8 15:37
 * @Version: 1.0
 */
@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    //新增
    //新增接口
    @PostMapping
    public AjaxResult<Boolean> add(@RequestBody ActivityAddVo activityAddVo) {
        return AjaxResult.success(activityService.add(activityAddVo));
    }

    //编辑
    @PutMapping("/{id}")
    public AjaxResult<Boolean> edit(@PathVariable Long id, @RequestBody ActivityEditVo activityEditVo) {
        if (activityEditVo.getId() == null) {
            activityEditVo.setId(id);
        }
        return AjaxResult.success(activityService.edit(activityEditVo));
    }


    //删除
    //判断是否有人报名
    @DeleteMapping("/{id}")
    public AjaxResult<Boolean> del(@PathVariable Long id) {
        return AjaxResult.success(activityService.del(id));
    }

    //查询
    @GetMapping
    public AjaxResult<PageResult<ActivityDetailVo>> listDetail(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                               @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                               @RequestParam(required = false) String activityName,
                                                               @RequestParam(required = false) Integer status) {
        return AjaxResult.success(activityService.listDetail(pageNum, pageSize, activityName, status));
    }


    //查询用户任务记录
    //关联用户表 ，活动表，任务表，
    @GetMapping("/user-tasks")
    public AjaxResult<PageResult<UserTaskListVo>> getUserTask(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return AjaxResult.success(activityService.getUserTask(pageNum, pageSize));
    }


    //用户活动报名
    //报名表，用户表，活动表
    @GetMapping("/user-joins")
    public AjaxResult<PageResult<UserJoinVo>> getUserJoin(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                          @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return AjaxResult.success(activityService.getUserJoin(pageNum, pageSize));
    }

    @GetMapping("/statistics")
    public AjaxResult<PageResult<ActivityStatVo>> getActivityStat(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                  @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                                  @RequestParam(required = false) String activityName) {
        return AjaxResult.success(activityService.getActivityStat(pageNum, pageSize, activityName));
    }

    @GetMapping("/{activityId}/department-statistics")
    public AjaxResult<PageResult<DeptTrainingStatVo>> getDeptStat(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                  @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                                  @PathVariable Long activityId) {
        return AjaxResult.success(activityService.getDeptStat(pageNum, pageSize, activityId));
    }

    @GetMapping("/{activityId}/tasks")
    public AjaxResult<List<TaskListVo>> listTasks(@PathVariable Long activityId) {
        return AjaxResult.success(activityService.listTasks(activityId));
    }

    @PostMapping("/{activityId}/tasks/{taskId}")
    public AjaxResult<Boolean> addTask(@PathVariable Long activityId, @PathVariable Long taskId) {
        return AjaxResult.success(activityService.addTask(activityId, taskId));
    }

    @PostMapping("/{activityId}/tasks/{taskId}/clone")
    public AjaxResult<Long> cloneTask(@PathVariable Long activityId, @PathVariable Long taskId) {
        Long newTaskId = activityService.cloneTask(activityId, taskId);
        if (newTaskId == null) {
            return AjaxResult.failed(ErrorEnum.FAILED, "Task not found.");
        }
        return AjaxResult.success(newTaskId);
    }

    @DeleteMapping("/{activityId}/tasks/{taskId}")
    public AjaxResult<Boolean> removeTask(@PathVariable Long activityId, @PathVariable Long taskId) {
        return AjaxResult.success(activityService.removeTask(activityId, taskId));
    }


    //获奖记录表
    //奖品表，获奖记录表


}

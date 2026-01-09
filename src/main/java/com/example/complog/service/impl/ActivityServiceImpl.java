package com.example.complog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.complog.domain.Activity;
import com.example.complog.domain.ActivityTaskRelation;
import com.example.complog.domain.Registration;
import com.example.complog.domain.Task;
import com.example.complog.domain.UserTaskRecord;
import com.example.complog.mapper.ActivityMapper;
import com.example.complog.mapper.ActivityTaskRelationMapper;
import com.example.complog.mapper.RegistrationMapper;
import com.example.complog.mapper.TaskMapper;
import com.example.complog.mapper.UserTaskRecordMapper;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
* @author Z1nk
* @description 针对表【activity】的数据库操作Service实现
* @createDate 2026-01-08 16:10:57
*/
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity>
    implements ActivityService{

    @Autowired
    private ActivityTaskRelationMapper activityTaskRelationMapper;
    @Autowired
    private RegistrationMapper registrationMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserTaskRecordMapper userTaskRecordMapper;

    @Override
    public boolean add(ActivityAddVo activityAddVo) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(activityAddVo, activity);
        int insert = baseMapper.insert(activity);

        if (insert > 0 && activityAddVo.getTaskIdList() != null) {
            for (Long taskId : activityAddVo.getTaskIdList()) {
                ActivityTaskRelation relation = new ActivityTaskRelation();
                relation.setActivityId(activity.getId());
                relation.setTaskId(taskId);
                activityTaskRelationMapper.insert(relation);
            }
        }
        return insert > 0;
    }

    @Override
    public boolean edit(ActivityEditVo activityEditVo) {
        Activity activity = baseMapper.selectById(activityEditVo.getId());
        if (activity == null) {
            return false;
        }
        BeanUtils.copyProperties(activityEditVo, activity);
        baseMapper.updateById(activity);

        QueryWrapper<ActivityTaskRelation> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("activity_id", activity.getId());
        activityTaskRelationMapper.delete(deleteWrapper);

        if (activityEditVo.getTaskIdList() != null) {
            for (Long taskId : activityEditVo.getTaskIdList()) {
                ActivityTaskRelation relation = new ActivityTaskRelation();
                relation.setActivityId(activity.getId());
                relation.setTaskId(taskId);
                activityTaskRelationMapper.insert(relation);
            }
        }
        return true;
    }

    @Override
    public boolean del(Long id) {
        Activity activity = baseMapper.selectById(id);
        if (activity == null) {
            return false;
        }
        activity.setIsDeleted(1);
        baseMapper.updateById(activity);

        QueryWrapper<ActivityTaskRelation> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("activity_id", id);
        activityTaskRelationMapper.delete(deleteWrapper);
        return true;
    }

    @Override
    public PageResult<ActivityDetailVo> listDetail(Integer pageNum, Integer pageSize, String activityName, Integer status) {
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        if (activityName != null && !activityName.isEmpty()) {
            // TODO: map activityName to the actual column once schema is finalized.
            wrapper.like("remark", activityName);
        }
        IPage<Activity> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<ActivityDetailVo> records = new ArrayList<>();
        for (Activity activity : page.getRecords()) {
            ActivityDetailVo vo = new ActivityDetailVo();
            BeanUtils.copyProperties(activity, vo);
            records.add(vo);
        }
        return PageResult.iPageHandle(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    public PageResult<UserTaskListVo> getUserTask(Integer pageNum, Integer pageSize) {
        QueryWrapper<UserTaskRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        IPage<UserTaskRecord> page = userTaskRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<UserTaskListVo> records = new ArrayList<>();

        Map<Long, Activity> activityMap = new HashMap<>();
        Map<Long, Task> taskMap = new HashMap<>();
        for (UserTaskRecord record : page.getRecords()) {
            if (record.getActivityId() != null && !activityMap.containsKey(record.getActivityId())) {
                activityMap.put(record.getActivityId(), baseMapper.selectById(record.getActivityId()));
            }
            if (record.getTaskId() != null && !taskMap.containsKey(record.getTaskId())) {
                taskMap.put(record.getTaskId(), taskMapper.selectById(record.getTaskId()));
            }
        }

        for (UserTaskRecord record : page.getRecords()) {
            UserTaskListVo vo = new UserTaskListVo();
            Activity activity = activityMap.get(record.getActivityId());
            Task task = taskMap.get(record.getTaskId());
            if (activity != null) {
                vo.setActivityName(activity.getRemark());
            }
            if (task != null) {
                vo.setTaskName(task.getName());
            }
            records.add(vo);
        }
        return PageResult.iPageHandle(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    public PageResult<UserJoinVo> getUserJoin(Integer pageNum, Integer pageSize) {
        QueryWrapper<Registration> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        IPage<Registration> page = registrationMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<UserJoinVo> records = new ArrayList<>();

        Map<Long, Activity> activityMap = new HashMap<>();
        for (Registration record : page.getRecords()) {
            if (record.getActivityId() != null && !activityMap.containsKey(record.getActivityId())) {
                activityMap.put(record.getActivityId(), baseMapper.selectById(record.getActivityId()));
            }
        }

        for (Registration record : page.getRecords()) {
            UserJoinVo vo = new UserJoinVo();
            Activity activity = activityMap.get(record.getActivityId());
            if (activity != null) {
                vo.setActivityName(activity.getRemark());
            }
            vo.setStatus(record.getStatus());
            records.add(vo);
        }
        return PageResult.iPageHandle(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    public PageResult<ActivityStatVo> getActivityStat(Integer pageNum, Integer pageSize, String activityName) {
        QueryWrapper<Activity> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        if (activityName != null && !activityName.isEmpty()) {
            wrapper.like("remark", activityName);
        }
        IPage<Activity> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<ActivityStatVo> records = new ArrayList<>();
        for (Activity activity : page.getRecords()) {
            QueryWrapper<Registration> joinWrapper = new QueryWrapper<>();
            joinWrapper.eq("is_deleted", 0).eq("activity_id", activity.getId());
            long totalJoin = registrationMapper.selectCount(joinWrapper);
            long completedCount = registrationMapper.selectCount(
                new QueryWrapper<Registration>()
                    .eq("is_deleted", 0)
                    .eq("activity_id", activity.getId())
                    .eq("status", 1)
            );

            ActivityStatVo vo = new ActivityStatVo();
            vo.setActivityId(activity.getId());
            vo.setActivityName(activity.getRemark());
            vo.setTotalJoin(totalJoin);
            vo.setCompletedCount(completedCount);
            if (totalJoin > 0) {
                vo.setCompletionRate((double) completedCount / (double) totalJoin);
            } else {
                vo.setCompletionRate(0.0);
            }
            records.add(vo);
        }
        return PageResult.iPageHandle(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    public PageResult<DeptTrainingStatVo> getDeptStat(Integer pageNum, Integer pageSize, Long activityId) {
        return PageResult.iPageHandle(0L, pageNum.longValue(), pageSize.longValue(), new ArrayList<>());
    }

    @Override
    public List<TaskListVo> listTasks(Long activityId) {
        QueryWrapper<ActivityTaskRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id", activityId).eq("is_deleted", 0);
        List<ActivityTaskRelation> relations = activityTaskRelationMapper.selectList(wrapper);
        if (relations.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> taskIds = new ArrayList<>();
        for (ActivityTaskRelation relation : relations) {
            taskIds.add(relation.getTaskId());
        }
        List<Task> tasks = taskMapper.selectBatchIds(taskIds);
        List<TaskListVo> result = new ArrayList<>();
        for (Task task : tasks) {
            TaskListVo vo = new TaskListVo();
            BeanUtils.copyProperties(task, vo);
            result.add(vo);
        }
        return result;
    }

    @Override
    public boolean addTask(Long activityId, Long taskId) {
        QueryWrapper<ActivityTaskRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id", activityId).eq("task_id", taskId).eq("is_deleted", 0);
        if (activityTaskRelationMapper.selectCount(wrapper) > 0) {
            return true;
        }
        ActivityTaskRelation relation = new ActivityTaskRelation();
        relation.setActivityId(activityId);
        relation.setTaskId(taskId);
        activityTaskRelationMapper.insert(relation);
        return true;
    }

    @Override
    public Long cloneTask(Long activityId, Long taskId) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            return null;
        }
        Task copy = new Task();
        BeanUtils.copyProperties(task, copy);
        copy.setId(null);
        copy.setCreatedAt(null);
        copy.setUpdatedAt(null);
        copy.setIsDeleted(null);
        taskMapper.insert(copy);
        addTask(activityId, copy.getId());
        return copy.getId();
    }

    @Override
    public boolean removeTask(Long activityId, Long taskId) {
        QueryWrapper<ActivityTaskRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id", activityId).eq("task_id", taskId);
        return activityTaskRelationMapper.delete(wrapper) > 0;
    }

}




package com.example.complog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.complog.domain.ActivityTaskRelation;
import com.example.complog.domain.Registration;
import com.example.complog.domain.Task;
import com.example.complog.domain.UserTaskRecord;
import com.example.complog.mapper.ActivityTaskRelationMapper;
import com.example.complog.mapper.RegistrationMapper;
import com.example.complog.mapper.TaskMapper;
import com.example.complog.mapper.UserTaskRecordMapper;
import com.example.complog.service.RegistrationService;
import com.example.complog.vo.UserSignDaysVo;
import com.example.complog.vo.UserSignDetailVo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
* @author Z1nk
* @description 针对表【registration】的数据库操作Service实现
* @createDate 2026-01-08 16:10:57
*/
@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration>
    implements RegistrationService{

    @Autowired
    private UserTaskRecordMapper userTaskRecordMapper;
    @Autowired
    private ActivityTaskRelationMapper activityTaskRelationMapper;
    @Autowired
    private TaskMapper taskMapper;

    @Override
    public boolean userJoin(Long userId, Long activityId) {
        QueryWrapper<Registration> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("activity_id", activityId).eq("is_deleted", 0);
        if (baseMapper.selectCount(wrapper) > 0) {
            return true;
        }
        Registration registration = new Registration();
        registration.setUserId(userId);
        registration.setActivityId(activityId);
        registration.setStatus(0);
        baseMapper.insert(registration);
        return true;
    }

    @Override
    public UserSignDaysVo getSignDays(Long userId, Long activityId) {
        QueryWrapper<UserTaskRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("activity_id", activityId).eq("is_deleted", 0);
        List<UserTaskRecord> records = userTaskRecordMapper.selectList(wrapper);

        UserSignDaysVo vo = new UserSignDaysVo();
        vo.setTaskRecords(records);
        vo.setTotalDays(records.size());

        Set<LocalDate> days = new HashSet<>();
        for (UserTaskRecord record : records) {
            if (record.getCreatedAt() != null) {
                days.add(record.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
        }
        List<LocalDate> dayList = new ArrayList<>(days);
        dayList.sort(Comparator.naturalOrder());
        int continuousDays = 0;
        for (int i = dayList.size() - 1; i >= 0; i--) {
            if (i == dayList.size() - 1) {
                continuousDays = 1;
            } else {
                LocalDate current = dayList.get(i);
                LocalDate next = dayList.get(i + 1);
                if (current.plusDays(1).equals(next)) {
                    continuousDays++;
                } else {
                    break;
                }
            }
        }
        vo.setContinuousDays(continuousDays);

        Task nextTask = getNextTask(activityId, records);
        vo.setNextTask(nextTask);
        return vo;
    }

    @Override
    public UserSignDetailVo sign(Long userId, Long activityId, Long taskId) {
        QueryWrapper<UserTaskRecord> existingWrapper = new QueryWrapper<>();
        existingWrapper.eq("user_id", userId)
            .eq("activity_id", activityId)
            .eq("task_id", taskId)
            .eq("is_deleted", 0);
        boolean exists = userTaskRecordMapper.selectCount(existingWrapper) > 0;

        if (!exists) {
            UserTaskRecord record = new UserTaskRecord();
            record.setUserId(userId);
            record.setActivityId(activityId);
            record.setTaskId(taskId);
            userTaskRecordMapper.insert(record);
        }

        UserSignDetailVo detailVo = new UserSignDetailVo();
        detailVo.setSuccess(!exists);
        detailVo.setCompleted(isActivityCompleted(userId, activityId));
        return detailVo;
    }

    private Task getNextTask(Long activityId, List<UserTaskRecord> records) {
        QueryWrapper<ActivityTaskRelation> relationWrapper = new QueryWrapper<>();
        relationWrapper.eq("activity_id", activityId).eq("is_deleted", 0);
        List<ActivityTaskRelation> relations = activityTaskRelationMapper.selectList(relationWrapper);
        if (relations.isEmpty()) {
            return null;
        }
        Set<Long> completed = new HashSet<>();
        for (UserTaskRecord record : records) {
            completed.add(record.getTaskId());
        }
        for (ActivityTaskRelation relation : relations) {
            if (!completed.contains(relation.getTaskId())) {
                return taskMapper.selectById(relation.getTaskId());
            }
        }
        return null;
    }

    private boolean isActivityCompleted(Long userId, Long activityId) {
        QueryWrapper<ActivityTaskRelation> relationWrapper = new QueryWrapper<>();
        relationWrapper.eq("activity_id", activityId).eq("is_deleted", 0);
        long totalTasks = activityTaskRelationMapper.selectCount(relationWrapper);
        if (totalTasks == 0) {
            return true;
        }
        QueryWrapper<UserTaskRecord> recordWrapper = new QueryWrapper<>();
        recordWrapper.eq("user_id", userId).eq("activity_id", activityId).eq("is_deleted", 0);
        long completed = userTaskRecordMapper.selectCount(recordWrapper);
        return completed >= totalTasks;
    }

}



package com.example.complog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.complog.domain.ActivityTaskRelation;
import com.example.complog.domain.Task;
import com.example.complog.mapper.ActivityTaskRelationMapper;
import com.example.complog.mapper.TaskMapper;
import com.example.complog.response.PageResult;
import com.example.complog.service.TaskService;
import com.example.complog.vo.TaskAddVo;
import com.example.complog.vo.TaskListVo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
* @author Z1nk
* @description 针对表【task】的数据库操作Service实现
* @createDate 2026-01-08 16:10:57
*/
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{

    @Autowired
    private ActivityTaskRelationMapper activityTaskRelationMapper;

    @Override
    public boolean add(TaskAddVo taskAddVo) {
        Task task = new Task();
        BeanUtils.copyProperties(taskAddVo, task);
        return baseMapper.insert(task) > 0;
    }

    @Override
    public boolean edit(TaskAddVo taskAddVo) {
        Task task = baseMapper.selectById(taskAddVo.getId());
        if (task == null) {
            return false;
        }
        BeanUtils.copyProperties(taskAddVo, task);
        baseMapper.updateById(task);
        return true;
    }

    @Override
    public boolean del(Long id) {
        QueryWrapper<ActivityTaskRelation> relationWrapper = new QueryWrapper<>();
        relationWrapper.eq("task_id", id).eq("is_deleted", 0);
        if (activityTaskRelationMapper.selectCount(relationWrapper) > 0) {
            return false;
        }
        Task task = baseMapper.selectById(id);
        if (task == null) {
            return false;
        }
        task.setIsDeleted(1);
        baseMapper.updateById(task);
        return true;
    }

    @Override
    public PageResult<TaskListVo> getList(Integer pageNum, Integer pageSize) {
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        IPage<Task> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<TaskListVo> records = new ArrayList<>();
        for (Task task : page.getRecords()) {
            TaskListVo vo = new TaskListVo();
            BeanUtils.copyProperties(task, vo);
            records.add(vo);
        }
        return PageResult.iPageHandle(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

}



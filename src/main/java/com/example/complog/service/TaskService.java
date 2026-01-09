package com.example.complog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.complog.domain.Task;
import com.example.complog.response.PageResult;
import com.example.complog.vo.TaskAddVo;
import com.example.complog.vo.TaskListVo;

/**
* @author Z1nk
* @description 针对表【task】的数据库操作Service
* @createDate 2026-01-08 16:10:57
*/
public interface TaskService extends IService<Task> {
    boolean add(TaskAddVo taskAddVo);

    boolean edit(TaskAddVo taskAddVo);

    boolean del(Long id);

    PageResult<TaskListVo> getList(Integer pageNum, Integer pageSize);

}

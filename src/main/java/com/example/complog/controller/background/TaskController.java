package com.example.complog.controller.background;

import com.example.complog.response.AjaxResult;
import com.example.complog.response.PageResult;
import com.example.complog.service.TaskService;
import com.example.complog.vo.TaskAddVo;
import com.example.complog.vo.TaskListVo;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @Author: Xintao Hu
 * @Desctription: TODO
 * @Date: Modified on 2026/1/8 15:37
 * @Version: 1.0
 */
@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    //新增接口
    @PostMapping
    public AjaxResult<Boolean> add(@RequestBody TaskAddVo taskAddVo) {
        return AjaxResult.success(taskService.add(taskAddVo));
    }

    //编辑接口
    @PutMapping("/{id}")
    public AjaxResult<Boolean> edit(@PathVariable Long id, @RequestBody TaskAddVo taskAddVo) {
        if (taskAddVo.getId() == null) {
            taskAddVo.setId(id);
        }
        return AjaxResult.success(taskService.edit(taskAddVo));
    }

    //删除接口
    @DeleteMapping("/{id}")
    public AjaxResult<Boolean> del(@PathVariable Long id) {
        return AjaxResult.success(taskService.del(id));
    }

    //查询接口
    @GetMapping
    public AjaxResult<PageResult<TaskListVo>> getList(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                         @RequestParam(required = false, defaultValue = "10") Integer pageSize
                                                         ) {
        return AjaxResult.success(taskService.getList(pageNum, pageSize));
    }



}

package com.example.complog.controller.background;

import com.example.complog.response.AjaxResult;
import com.example.complog.response.PageResult;
import com.example.complog.service.DepartmentService;
import com.example.complog.vo.DeptAddVo;
import com.example.complog.vo.DeptListVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public AjaxResult<Boolean> add(@RequestBody DeptAddVo deptAddVo) {
        return AjaxResult.success(departmentService.add(deptAddVo));
    }

    @GetMapping
    public AjaxResult<PageResult<DeptListVo>> list(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                   @RequestParam(required = false) String name) {
        return AjaxResult.success(departmentService.list(pageNum, pageSize, name));
    }
}

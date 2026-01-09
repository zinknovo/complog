package com.example.complog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.complog.domain.Department;
import com.example.complog.response.PageResult;
import com.example.complog.vo.DeptAddVo;
import com.example.complog.vo.DeptListVo;

public interface DepartmentService extends IService<Department> {
    boolean add(DeptAddVo deptAddVo);

    PageResult<DeptListVo> list(Integer pageNum, Integer pageSize, String name);
}

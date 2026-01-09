package com.example.complog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.complog.domain.Department;
import com.example.complog.mapper.DepartmentMapper;
import com.example.complog.response.PageResult;
import com.example.complog.service.DepartmentService;
import com.example.complog.vo.DeptAddVo;
import com.example.complog.vo.DeptListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department>
    implements DepartmentService {

    @Override
    public boolean add(DeptAddVo deptAddVo) {
        Department department = new Department();
        BeanUtils.copyProperties(deptAddVo, department);
        return baseMapper.insert(department) > 0;
    }

    @Override
    public PageResult<DeptListVo> list(Integer pageNum, Integer pageSize, String name) {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        IPage<Department> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<DeptListVo> records = new ArrayList<>();
        for (Department department : page.getRecords()) {
            DeptListVo vo = new DeptListVo();
            BeanUtils.copyProperties(department, vo);
            records.add(vo);
        }
        return PageResult.iPageHandle(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }
}

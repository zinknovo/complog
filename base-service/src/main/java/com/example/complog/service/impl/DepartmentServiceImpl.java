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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department>
    implements DepartmentService {

    @Override
    @CacheEvict(value = "departments", allEntries = true) // Clear cache when adding / 添加时清除缓存
    public boolean add(DeptAddVo deptAddVo) {
        // Check if department name already exists / 检查部门名称是否已存在
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("name", deptAddVo.getName());
        wrapper.eq("is_deleted", 0);
        Department existing = baseMapper.selectOne(wrapper);
        if (existing != null) {
            throw new RuntimeException("Department name already exists / 部门名称已存在: " + deptAddVo.getName());
        }

        Department department = new Department();
        BeanUtils.copyProperties(deptAddVo, department);
        return baseMapper.insert(department) > 0;
    }

    @Override
    @Cacheable(value = "departments", key = "'list_' + #pageNum + '_' + #pageSize + '_' + (#name != null ? #name : '')") // Cache query results / 缓存查询结果
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

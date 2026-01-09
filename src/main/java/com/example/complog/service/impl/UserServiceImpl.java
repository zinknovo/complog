package com.example.complog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.complog.domain.Department;
import com.example.complog.domain.User;
import com.example.complog.mapper.DepartmentMapper;
import com.example.complog.mapper.UserMapper;
import com.example.complog.response.PageResult;
import com.example.complog.service.UserService;
import com.example.complog.vo.UserAddVo;
import com.example.complog.vo.UserListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public boolean add(UserAddVo userAddVo) {
        User user = new User();
        BeanUtils.copyProperties(userAddVo, user);
        return baseMapper.insert(user) > 0;
    }

    @Override
    public PageResult<UserListVo> list(Integer pageNum, Integer pageSize, String name, Long deptId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (deptId != null) {
            wrapper.eq("dept_id", deptId);
        }
        IPage<User> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        Map<Long, String> deptNameMap = new HashMap<>();
        Set<Long> deptIds = new HashSet<>();
        for (User user : page.getRecords()) {
            if (user.getDeptId() != null) {
                deptIds.add(user.getDeptId());
            }
        }
        if (!deptIds.isEmpty()) {
            List<Department> departments = departmentMapper.selectBatchIds(deptIds);
            for (Department department : departments) {
                deptNameMap.put(department.getId(), department.getName());
            }
        }

        List<UserListVo> records = new ArrayList<>();
        for (User user : page.getRecords()) {
            UserListVo vo = new UserListVo();
            BeanUtils.copyProperties(user, vo);
            if (user.getDeptId() != null) {
                vo.setDeptName(deptNameMap.get(user.getDeptId()));
            }
            records.add(vo);
        }
        return PageResult.iPageHandle(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }
}

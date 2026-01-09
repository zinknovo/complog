package com.example.complog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.complog.domain.User;
import com.example.complog.response.PageResult;
import com.example.complog.vo.UserAddVo;
import com.example.complog.vo.UserListVo;

public interface UserService extends IService<User> {
    boolean add(UserAddVo userAddVo);

    PageResult<UserListVo> list(Integer pageNum, Integer pageSize, String name, Long deptId);
}

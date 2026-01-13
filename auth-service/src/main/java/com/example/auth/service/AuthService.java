package com.example.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.auth.mapper.UserMapper;
import com.example.auth.model.LoginRequest;
import com.example.auth.model.LoginResponse;
import com.example.auth.model.User;
import com.example.auth.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 认证服务
 */
@Service
public class AuthService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", request.getPhone());
        wrapper.eq("is_deleted", 0);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 验证密码（这里简化处理，实际应该使用BCrypt等加密）
        // 注意：当前数据库可能没有password字段，需要先添加
        String hashedPassword = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
        if (user.getPassword() != null && !user.getPassword().equals(hashedPassword)) {
            throw new RuntimeException("密码错误");
        }
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), 
                user.getRole() != null ? user.getRole() : "user");
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        response.setUser(userInfo);
        
        return response;
    }
    
    /**
     * 根据token获取用户信息
     */
    public User getUserByToken(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            return userMapper.selectById(userId);
        } catch (Exception e) {
            return null;
        }
    }
}
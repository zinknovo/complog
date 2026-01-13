package com.example.auth.controller;

import com.example.auth.model.ApiResponse;
import com.example.auth.model.LoginRequest;
import com.example.auth.model.LoginResponse;
import com.example.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 验证token（用于其他服务调用）
     */
    @GetMapping("/verify")
    public ApiResponse<Boolean> verify(@RequestHeader("Authorization") String token) {
        try {
            // 移除 "Bearer " 前缀
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            authService.getUserByToken(token);
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error(false);
        }
    }
}
package com.example.auth.model;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginResponse {
    private String token;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private Long deptId;
        private String role;
        private Integer status;
    }
}
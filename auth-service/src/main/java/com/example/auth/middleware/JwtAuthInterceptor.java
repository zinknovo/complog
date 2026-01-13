package com.example.auth.middleware;

import com.example.auth.model.User;
import com.example.auth.service.AuthService;
import com.example.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT认证拦截器
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthService authService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 排除登录接口
        String path = request.getRequestURI();
        if (path.contains("/login") || path.contains("/verify")) {
            return true;
        }
        
        // 获取token
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        
        // 移除 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 验证token
        try {
            if (jwtUtil.isTokenExpired(token)) {
                response.setStatus(401);
                return false;
            }
            
            // 将用户信息存入request
            User user = authService.getUserByToken(token);
            if (user != null) {
                request.setAttribute("currentUser", user);
                request.setAttribute("userId", user.getId());
            }
            
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}
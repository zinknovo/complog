package com.example.complog.tenant.interceptor;

import com.example.complog.tenant.context.TenantContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tenant interceptor / 租户拦截器
 * Extract tenant ID from request and store in context / 从请求中提取租户ID并存储到上下文
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    private static final String TENANT_ID_HEADER = "X-Tenant-Id";
    private static final String DEFAULT_TENANT = "default";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Get tenant ID from request header / 从请求头获取租户ID
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        
        // If not in header, try to get from JWT token (if available)
        // 如果请求头没有，尝试从 JWT token 获取（如果可用）
        if (tenantId == null || tenantId.isEmpty()) {
            // TODO: Extract from JWT token if needed
            // 如果需要，可以从 JWT token 中提取
            tenantId = DEFAULT_TENANT; // Default tenant for now / 暂时使用默认租户
        }
        
        // Store in context / 存储到上下文
        TenantContext.setTenantId(tenantId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // Clear tenant context after request / 请求结束后清除租户上下文
        TenantContext.clear();
    }
}
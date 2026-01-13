package com.example.policy.tenant.context;

/**
 * Tenant context / 租户上下文
 * Store current tenant ID in ThreadLocal / 在 ThreadLocal 中存储当前租户ID
 */
public class TenantContext {
    
    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();
    
    /**
     * Set current tenant ID / 设置当前租户ID
     */
    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }
    
    /**
     * Get current tenant ID / 获取当前租户ID
     */
    public static String getTenantId() {
        return TENANT_ID.get();
    }
    
    /**
     * Clear tenant ID / 清除租户ID
     */
    public static void clear() {
        TENANT_ID.remove();
    }
}
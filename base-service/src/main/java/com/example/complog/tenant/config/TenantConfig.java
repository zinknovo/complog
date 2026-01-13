package com.example.complog.tenant.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.example.complog.tenant.context.TenantContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Tenant configuration / 多租户配置
 * Auto-add tenant_id condition to all queries / 自动为所有查询添加 tenant_id 条件
 */
@Configuration
public class TenantConfig {
    
    /**
     * Configure MyBatis-Plus interceptor with tenant support / 配置支持多租户的 MyBatis-Plus 拦截器
     * This replaces the one in MybatisPlusConfig / 这个替换了 MybatisPlusConfig 中的配置
     */
    @Bean
    @Primary
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // Tenant interceptor must be added first / 租户拦截器必须最先添加
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(new com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler() {
            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }
            
            @Override
            public Object getTenantId() {
                String tenantId = TenantContext.getTenantId();
                return tenantId != null ? tenantId : "default";
            }
            
            @Override
            public boolean ignoreTable(String tableName) {
                // All tables need tenant filtering / 所有表都需要租户过滤
                return false;
            }
        });
        interceptor.addInnerInterceptor(tenantInterceptor);
        
        // Pagination plugin / 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        
        // Optimistic locking plugin / 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        return interceptor;
    }
}
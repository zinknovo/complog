package com.example.policy.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration / Kafka配置
 */
@Configuration
public class KafkaConfig {

    /**
     * Create topic for policy events / 创建制度事件主题
     */
    @Bean
    public NewTopic policyEventsTopic() {
        return TopicBuilder.name("policy-events")
                .partitions(3)  // 3个分区
                .replicas(1)    // 1个副本（单机环境）
                .build();
    }
}
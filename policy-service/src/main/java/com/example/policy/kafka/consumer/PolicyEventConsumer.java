package com.example.policy.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Policy event consumer / 制度事件消费者
 * 消费来自其他服务的制度相关事件
 */
@Component
public class PolicyEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PolicyEventConsumer.class);

    /**
     * Consume policy events / 消费制度事件
     */
    @KafkaListener(topics = "policy-events", groupId = "policy-service-group")
    public void consumePolicyEvents(String message) {
        logger.info("Received policy event: {}", message);
        // TODO: 处理制度事件
        // 例如：更新缓存、同步数据等
    }
}
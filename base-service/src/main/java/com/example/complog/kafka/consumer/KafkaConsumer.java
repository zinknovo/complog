package com.example.complog.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer / Kafka消息消费者
 */
@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    /**
     * Consume policy events / 消费制度事件
     */
    @KafkaListener(topics = "policy-events", groupId = "base-service-group")
    public void consumePolicyEvents(String message) {
        logger.info("Received policy event: {}", message);
        // TODO: 处理制度事件
        // 例如：更新缓存、同步数据等
    }

    /**
     * Consume user events / 消费用户事件
     */
    @KafkaListener(topics = "user-events", groupId = "base-service-group")
    public void consumeUserEvents(String message) {
        logger.info("Received user event: {}", message);
        // TODO: 处理用户事件
    }

    /**
     * Consume department events / 消费部门事件
     */
    @KafkaListener(topics = "department-events", groupId = "base-service-group")
    public void consumeDepartmentEvents(String message) {
        logger.info("Received department event: {}", message);
        // TODO: 处理部门事件
    }
}
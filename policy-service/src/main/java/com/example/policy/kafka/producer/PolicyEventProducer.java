package com.example.policy.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Policy event producer / 制度事件生产者
 * 发布制度相关事件到 Kafka
 */
@Component
public class PolicyEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PolicyEventProducer.class);
    private static final String TOPIC = "policy-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired(required = false)
    private ObjectMapper objectMapper;

    public PolicyEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        // 如果没有注入 ObjectMapper，创建默认的
        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }
    }

    /**
     * Publish policy created event / 发布制度创建事件
     */
    public void publishPolicyCreated(Long policyId, String policyName) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "POLICY_CREATED");
        event.put("policyId", policyId);
        event.put("policyName", policyName);
        event.put("timestamp", System.currentTimeMillis());
        
        sendEvent(policyId.toString(), event);
    }

    /**
     * Publish policy updated event / 发布制度更新事件
     */
    public void publishPolicyUpdated(Long policyId, String policyName) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "POLICY_UPDATED");
        event.put("policyId", policyId);
        event.put("policyName", policyName);
        event.put("timestamp", System.currentTimeMillis());
        
        sendEvent(policyId.toString(), event);
    }

    /**
     * Publish policy published event / 发布制度发布事件
     */
    public void publishPolicyPublished(Long policyId, String policyName, String version) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "POLICY_PUBLISHED");
        event.put("policyId", policyId);
        event.put("policyName", policyName);
        event.put("version", version);
        event.put("timestamp", System.currentTimeMillis());
        
        sendEvent(policyId.toString(), event);
    }

    /**
     * Send event to Kafka / 发送事件到 Kafka
     */
    private void sendEvent(String key, Map<String, Object> event) {
        try {
            ObjectMapper mapper = objectMapper != null ? objectMapper : new ObjectMapper();
            String message = mapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, key, message);
            logger.info("Published event to Kafka: topic={}, key={}, eventType={}",
                    TOPIC, key, event.get("eventType"));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize event: {}", event, e);
        }
    }
}
package com.example.complog.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Kafka producer / Kafka消息生产者
 */
@Component
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send message to topic / 发送消息到主题
     * @param topic Topic name / 主题名称
     * @param key Message key / 消息键
     * @param message Message content / 消息内容
     */
    public void sendMessage(String topic, String key, String message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("Message sent successfully: topic={}, key={}, offset={}",
                        topic, key, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.error("Failed to send message: topic={}, key={}, error={}",
                        topic, key, ex.getMessage(), ex);
            }
        });
    }

    /**
     * Send message without key / 发送消息（无键）
     */
    public void sendMessage(String topic, String message) {
        sendMessage(topic, null, message);
    }
}
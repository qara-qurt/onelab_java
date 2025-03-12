package org.onelab.user_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.onelab.user_service.utils.KafkaTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic failedPaid() {
        return new NewTopic(KafkaTopics.FAILED_PAID, 1, (short) 1);
    }

    @Bean
    public NewTopic successPaid() {
        return new NewTopic(KafkaTopics.SUCCESS_PAID, 1, (short) 1);
    }
}

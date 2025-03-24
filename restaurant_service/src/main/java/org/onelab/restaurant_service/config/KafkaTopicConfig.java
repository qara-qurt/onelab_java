package org.onelab.restaurant_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.onelab.common_lib.kafka.KafkaTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic withdrawOrder() {
        return new NewTopic(KafkaTopics.WITHDRAW_ORDER, 1, (short) 1);
    }

}

package org.onelab.gateway_cli_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.onelab.common_lib.kafka.KafkaTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic fillBalance() {
        return new NewTopic(KafkaTopics.USER_FILL_BALANCE, 1, (short) 1);
    }
}

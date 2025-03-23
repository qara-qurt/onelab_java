//package org.onelab.restaurant_service.kafka;
//
//import lombok.AllArgsConstructor;
//import org.onelab.restaurant_service.utils.KafkaTopics;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@AllArgsConstructor
//public class KafkaProducer {
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public void withdrawOrder(Long orderId, Long userId, double totalPrice) {
//        try {
//            String price = String.valueOf(totalPrice);
//            List<String> payloadList = List.of(String.valueOf(orderId), price);
//
//            kafkaTemplate.send(KafkaTopics.WITHDRAW_ORDER, String.valueOf(userId), payloadList);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Error:" + e.getMessage());
//        }
//    }
//}

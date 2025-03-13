package org.onelab.restaurant_service.utils;

public final class KafkaTopics {
    private KafkaTopics() {
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    public static final String GROUP_ID = "restaurant-group";
    public static final String WITHDRAW_ORDER = "order.withdraw";
}

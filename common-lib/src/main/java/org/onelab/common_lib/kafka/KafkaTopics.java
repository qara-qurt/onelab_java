package org.onelab.common_lib.kafka;

public final class KafkaTopics {
    private KafkaTopics() {
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    public static final String RESTAURANT_GROUP_ID = "restaurant-group";
    public static final String CLI_GROUP_ID = "cli-group";
    public static final String USER_GROUP_ID = "user-group";
    public static final String ORDER_WORKER_GROUP_ID = "order-worker-group";

    public static final String WITHDRAW_ORDER = "order.withdraw";

    public static final String FAILED_PAID = "user.payment_failed";
    public static final String SUCCESS_PAID = "user.payment_success";
    public static final String USER_FILL_BALANCE = "user.fill_balance";
}


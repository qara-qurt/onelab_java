package org.onelab.camunda_service.utils;

public final class KafkaTopics {
    private KafkaTopics() {
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    public static final String GROUP_ID = "camunda-group";
    public static final String WITHDRAW_ORDER = "order.withdraw";
    public static final String FAILED_PAID = "user.payment_failed";
    public static final String SUCCESS_PAID = "user.payment_success";
}

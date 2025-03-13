package org.onelab.gateway_cli_service.utils;

public final class KafkaTopics {
    private KafkaTopics() {
        throw new UnsupportedOperationException("Utility class should not be instantiated.");
    }

    public static final String GROUP_ID = "cli-group";
    public static final String USER_FILL_BALANCE = "user.fill_balance";
    public static final String FAILED_PAID = "user.payment_failed";
    public static final String SUCCESS_PAID = "user.payment_success";

}

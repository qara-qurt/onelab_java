package org.onelab.gateway_cli_service.service;

public interface PaymentService {
    String getFailedPayments();
    String getSuccessfulPayments();
}

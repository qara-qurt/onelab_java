package org.onelab.camunda_service.dto;

import java.io.Serializable;

public enum OrderStatus implements Serializable {
    NEW, PROCESSING, COMPLETED, CANCELLED, PAID
}

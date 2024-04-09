package com.example.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumptionRequest {
    private Long orderId;
    private double amount;
    private String customerEmail;
    private String couponCode;
}

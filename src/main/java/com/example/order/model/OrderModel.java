package com.example.order.model;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

@Data
public class OrderModel {
    @NotNull
    private String productCode;
    @NotNull
    private String customerEmail;
    private double price;
    @NotNull
    private String cardNumber;
    private String couponCode;
}

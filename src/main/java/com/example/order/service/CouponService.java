package com.example.order.service;

import com.example.order.model.ConsumptionRequest;
import org.springframework.web.bind.annotation.RequestParam;

public interface CouponService {
    Double consumeCoupon(ConsumptionRequest consumptionRequest);

    Boolean isValidCoupon(String couponCode);

    Double calcAmountAfterCouponDiscount(String couponCode, double amount);
}

package com.example.order.service.impl;

import com.example.order.model.ConsumptionRequest;
import com.example.order.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final RestTemplate restTemplate;
    @Override
    public Double consumeCoupon(ConsumptionRequest consumptionRequest) {
        String url="http://localhost:8083/coupon/consume";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ConsumptionRequest> requestEntity = new HttpEntity<>(consumptionRequest, headers);

        ResponseEntity<Double> response = restTemplate.postForEntity(url, requestEntity, Double.class);

        return response.getBody();
    }

    @Override
    public Boolean isValidCoupon(String couponCode) {
        String url = "http://localhost:8083/coupon/validation?couponCode=" + couponCode;
        return restTemplate.getForObject(url, Boolean.class);
    }

    @Override
    public Double calcAmountAfterCouponDiscount(String couponCode, double amount) {
        String url = "http://localhost:8083/coupon/discount?couponCode=" + couponCode + "&amount=" + amount;
        return restTemplate.getForObject(url, Double.class);
    }
}

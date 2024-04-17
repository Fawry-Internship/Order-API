package com.example.order.service.impl;

import com.example.order.entity.Order;
import com.example.order.exception.ApiCallFailedException;
import com.example.order.exception.RecordNotFoundException;
import com.example.order.mapper.OrderMapper;
import com.example.order.model.ConsumptionRequest;
import com.example.order.model.EmailModel;
import com.example.order.model.OrderModel;
import com.example.order.model.TransactionRequest;
import com.example.order.repository.OrderRepository;
import com.example.order.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BankService bankService;
    private final CouponService couponService;
    private final NotificationService notificationService;
    private final StockService stockService;
    private final OrderMapper mapper;
    @Value("${email.merchant}")
    private String merchantEmail;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public String createOrder(OrderModel orderModel) {
        log.info("Creating order: {}", orderModel);
        try {
            if(orderModel.getCouponCode() != null){
                validateCoupon(orderModel.getCouponCode());
                log.info("coupon is valid");
            }
            validateProductStockAvailability(orderModel.getProductCode());
            log.info("Product is available in stock. Proceeding with order creation.");

            double amountAfterDiscount = orderModel.getPrice();
            if(orderModel.getCouponCode() != null){
                amountAfterDiscount =  couponService.calcAmountAfterCouponDiscount(orderModel.getCouponCode(), orderModel.getPrice());
                log.info("Amount price After discount {}", amountAfterDiscount);
            }

            applyWithdrawTransactionForCustomer(amountAfterDiscount, orderModel);
            applyDepositTransactionForMerchant(amountAfterDiscount);

            ResponseEntity<String> consumeResponse = stockService.consume(orderModel.getProductCode());
            log.info("Stock consumed successfully. Response: {}", consumeResponse.getBody());

            Order order = mapper.toEntity(orderModel);
            order.setCreationDate(LocalDate.now());
            order.setCustomerEmail(orderModel.getCustomerEmail());
            order.setPrice(amountAfterDiscount);
            orderRepository.save(order);

            if(orderModel.getCouponCode() != null){
                ConsumptionRequest consumptionRequest = new ConsumptionRequest(order.getId(), orderModel.getPrice(), order.getCustomerEmail(), order.getCouponCode());
                log.info("Consumption coupon request {}", consumptionRequest);
                double amount = couponService.consumeCoupon(consumptionRequest);
                log.info("Coupon consumed successfully. Amount: {}", amount);
            }

            // Send notification
            EmailModel emailModel = new EmailModel(order.getCustomerEmail(), "Order creation", order.getProductCode(), String.valueOf(amountAfterDiscount), String.valueOf(order.getCreationDate()));
            sendEmail(order.getCustomerEmail(), emailModel); //customer
            sendEmail(merchantEmail, emailModel); //merchant
            return "success";
        } catch (HttpClientErrorException e) {
            log.error("Error occurred during order creation: {}", e.getMessage());
            return "error: " + e.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("Error occurred during order creation: {}", e.getMessage());
            throw new ApiCallFailedException("Error occurred during order creation " + e);
        }
    }

    private void applyDepositTransactionForMerchant(double amount) {
        TransactionRequest transactionRequestMerchant = new TransactionRequest();
        transactionRequestMerchant.setCardNumber("3830828855753986");
        transactionRequestMerchant.setAmount(amount);
        bankService.deposit(transactionRequestMerchant);
        log.info(" merchant deposit done");
    }
    private void applyWithdrawTransactionForCustomer(double amount, OrderModel orderModel) {
        TransactionRequest transactionRequestCustomer = new TransactionRequest();
        transactionRequestCustomer.setAmount(amount);
        transactionRequestCustomer.setCardNumber(orderModel.getCardNumber());
        ResponseEntity<String> withdrawResponse = bankService.withdraw(transactionRequestCustomer);
        log.info("Customer withdrawal request processed successfully. Response: {}", withdrawResponse.getBody());
    }

    private void validateCoupon(String couponCode) {
        if(!couponService.isValidCoupon(couponCode)){
            log.error("coupon not valid. Order creation aborted.");
            throw new RecordNotFoundException("coupon not valid. Order creation aborted.");
        }
    }

    private void validateProductStockAvailability(String productCode) {
        if(!stockService.checkAvailability(productCode)){
            log.error("Product is not available in stock. Order creation aborted.");
            throw new RecordNotFoundException("Product is not available in stock. Order creation aborted.");
        }
    }

    private void sendEmail(String to, EmailModel emailModel) {
        emailModel.setTo(to);
        notificationService.sendEmail(emailModel);
        log.info("Notification sent successfully!");
    }

    @Override
    public List<Order> findOrderByCustomer_email(String customer_email) {
        return orderRepository.findAllByCustomerEmail(customer_email);
    }

    @Override
    public List<Order> findAllOrderBetween(LocalDate from, LocalDate to) {
        return orderRepository.findAllByCreationDateBetween(from, to);
    }
}

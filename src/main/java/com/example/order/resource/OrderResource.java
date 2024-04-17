package com.example.order.resource;

import com.example.order.entity.Order;
import com.example.order.model.OrderModel;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/order/")
@RequiredArgsConstructor
public class OrderResource {
    private final OrderService orderService;
    @PostMapping("create")
    public ResponseEntity<String> createOrder(@Valid @RequestBody OrderModel orderModel){
        return new ResponseEntity<>(orderService.createOrder(orderModel), HttpStatus.CREATED);
    }

    @GetMapping("findByCustomerEmail")
    public List<Order> findOrderByCustomerEmail(@Valid@RequestParam String customerEmail){
        return orderService.findOrderByCustomer_email(customerEmail);
    }
    @GetMapping("ByDateRange")
    public List<Order> searchByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to
    ){
        return orderService.findAllOrderBetween(from,to);
    }
}

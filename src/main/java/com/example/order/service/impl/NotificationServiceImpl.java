package com.example.order.service.impl;

import com.example.order.model.EmailModel;
import com.example.order.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final AmqpTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void sendEmail(EmailModel emailModel) {
        try {
            String json = objectMapper.writeValueAsString(emailModel);
            rabbitTemplate.convertAndSend("orderQueue", json);
        } catch (JsonProcessingException e) {
            log.error("Error converting EmailModel to JSON: {}", e.getMessage());
        }
    }
}

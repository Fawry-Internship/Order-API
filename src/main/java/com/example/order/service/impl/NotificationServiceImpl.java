package com.example.order.service.impl;

import com.example.order.model.EmailModel;
import com.example.order.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final AmqpTemplate rabbitTemplate;
    @Value("${rabbitmq.queue}")
    private String queueName;

    @Override
    public void sendEmail(EmailModel emailModel) {
        rabbitTemplate.convertAndSend(queueName, emailModel);
    }
}

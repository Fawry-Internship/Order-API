package com.example.order.service;

import com.example.order.model.EmailModel;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
     void sendEmail( EmailModel emailRequest) ;
}

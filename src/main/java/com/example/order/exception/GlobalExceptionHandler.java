package com.example.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleRecordNotFoundException(RecordNotFoundException e){
        ErrorDetails errorDetails = new ErrorDetails(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                new Date()
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApiCallFailedException.class)
    public ResponseEntity<ErrorDetails> handleRecordNotFoundException(ApiCallFailedException e){
        ErrorDetails errorDetails = new ErrorDetails(
                e.getMessage(),
                HttpStatus.NOT_IMPLEMENTED.value(),
                new Date()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_IMPLEMENTED);
    }

}

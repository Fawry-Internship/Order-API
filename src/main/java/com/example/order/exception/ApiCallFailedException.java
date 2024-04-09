package com.example.order.exception;

public class ApiCallFailedException extends RuntimeException{
    public ApiCallFailedException() {
        super();
    }

    public ApiCallFailedException(String message) {
        super(message);
    }
}

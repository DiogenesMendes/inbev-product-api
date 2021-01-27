package com.inbev.productapi.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String string) {
        super(string);
    }
}

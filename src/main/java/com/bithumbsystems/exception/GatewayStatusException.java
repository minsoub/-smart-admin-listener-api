package com.bithumbsystems.exception;


public class GatewayStatusException extends RuntimeException {
    public GatewayStatusException(String errorCode) {
        super(errorCode);
    }
}

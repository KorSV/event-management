package com.ksv.exceptions;

public class EventFullException extends DomainException {
    public EventFullException(String message) {
        super(message);
    }
}

package com.ksv.exceptions;

public class UserNotRegisteredException extends DomainException {
    public UserNotRegisteredException(String message) {
        super(message);
    }
}

package com.ksv.exceptions;

public class MemberAlreadyRegisterException extends DomainException {
    public MemberAlreadyRegisterException(String message) {
        super(message);
    }
}

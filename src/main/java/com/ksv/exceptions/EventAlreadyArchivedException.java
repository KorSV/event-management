package com.ksv.exceptions;

public class EventAlreadyArchivedException extends DomainException {
    public EventAlreadyArchivedException(String message) {
        super(message);
    }
}

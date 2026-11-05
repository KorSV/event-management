package com.ksv.exceptions;

public class CannotRegisterOnArchivedEventException extends DomainException {
    public CannotRegisterOnArchivedEventException(String message) {
        super(message);
    }
}

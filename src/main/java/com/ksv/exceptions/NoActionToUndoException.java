package com.ksv.exceptions;

public class NoActionToUndoException extends DomainException {
    public NoActionToUndoException(String message) {
        super(message);
    }
}

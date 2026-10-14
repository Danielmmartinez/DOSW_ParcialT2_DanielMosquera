package edu.dosw.parcial.controller.handlers.exceptions;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
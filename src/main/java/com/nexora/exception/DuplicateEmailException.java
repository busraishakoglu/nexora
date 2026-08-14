package com.nexora.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("A customer already exists with email: " + email);
    }
}

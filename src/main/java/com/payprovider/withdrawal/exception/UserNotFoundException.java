package com.payprovider.withdrawal.exception;

public class UserNotFoundException extends TransactionException {

    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public UserNotFoundException(String errorMessage, Object... params) {
        super(errorMessage);
    }
}

package com.payprovider.withdrawal.exception;

public class TransactionException extends Exception {

    public TransactionException(String errorMessage) {
        super(errorMessage);
    }
}

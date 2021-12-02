package com.miihe.bill.exception;

public class AccountIdNotFoundException extends RuntimeException {

    public AccountIdNotFoundException(String message) {
        super(message);
    }
}

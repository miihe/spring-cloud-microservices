package com.miihe.deposit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DepositServiceException extends RuntimeException{

    public DepositServiceException(String message) {
        super(message);
    }
}

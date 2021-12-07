package com.miihe.transfer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class TransferServiceException extends RuntimeException {

    public TransferServiceException(String message) {
        super(message);
    }
}

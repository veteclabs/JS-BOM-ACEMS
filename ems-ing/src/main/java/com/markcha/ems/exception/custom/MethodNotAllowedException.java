package com.markcha.ems.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class MethodNotAllowedException extends RuntimeException {
    public MethodNotAllowedException(String message) {
        super(message);
    }
}

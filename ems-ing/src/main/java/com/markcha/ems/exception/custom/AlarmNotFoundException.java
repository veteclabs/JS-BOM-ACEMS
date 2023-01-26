package com.markcha.ems.exception.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlarmNotFoundException extends RuntimeException {
    public AlarmNotFoundException(String message) {
        super(message);
    }
}

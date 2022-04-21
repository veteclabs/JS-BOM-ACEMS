package com.markcha.ems.exception;

import com.markcha.ems.exception.custom.AlarmNotFoundException;
import com.markcha.ems.exception.custom.MethodNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RestController
public class RestErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestErrorHandler.class);

//    @ExceptionHandler(Exception.class)
//    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
//        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
//                request.getDescription(false));
//        return new ResponseEntity(exceptionResponse, INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(AlarmNotFoundException.class)
    public final ResponseEntity<Object> handleAlarmNotFoundException(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
                request.getDescription(false));
        ResponseEntity responseEntity = new ResponseEntity(exceptionResponse, NOT_FOUND);

        return responseEntity;
    }
    @ExceptionHandler(MethodNotAllowedException.class)
    public final ResponseEntity<Object> handleMethodNotAllowedException(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), ex.getMessage(),
                request.getDescription(false));
        ResponseEntity responseEntity = new ResponseEntity(exceptionResponse, METHOD_NOT_ALLOWED);

        return responseEntity;
    }
}
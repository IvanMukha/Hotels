package com.mukha.hotels.exception;

import org.springframework.http.HttpStatus;

public class UnknownHistogramParameterException extends GlobalServiceException {
    public UnknownHistogramParameterException(String param) {
        super("Unknown histogram parameter: " + param, HttpStatus.BAD_REQUEST);
    }
}

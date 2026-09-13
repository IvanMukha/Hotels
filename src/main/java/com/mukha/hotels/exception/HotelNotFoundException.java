package com.mukha.hotels.exception;

import org.springframework.http.HttpStatus;

public class HotelNotFoundException extends GlobalServiceException {
    public HotelNotFoundException(Long id) {
        super("Hotel with id: " + id + " not found", HttpStatus.NOT_FOUND);
    }
}

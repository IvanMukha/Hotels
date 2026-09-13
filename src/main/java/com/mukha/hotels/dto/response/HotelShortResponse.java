package com.mukha.hotels.dto.response;

public record HotelShortResponse(
        Long id,

        String name,

        String description,

        String address,

        String phone) {
}

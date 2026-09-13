package com.mukha.hotels.dto.response;

import com.mukha.hotels.dto.AddressDto;
import com.mukha.hotels.dto.ArrivalTimeDto;
import com.mukha.hotels.dto.ContactsDto;

import java.util.Set;

public record HotelDetailResponse(
        Long id,

        String name,

        String description,

        String brand,

        AddressDto address,

        ContactsDto contacts,

        ArrivalTimeDto arrivalTime,

        Set<String> amenities) {
}

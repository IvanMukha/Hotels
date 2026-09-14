package com.mukha.hotels.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Detailed geopolitical and physical address location data")
public record AddressDto(

        @Schema(description = "Physical house or building number", example = "9")
        @NotBlank(message = "House number is required")
        @Size(max = 50, message = "House number must not exceed {max} characters")
        String houseNumber,

        @Schema(description = "Name of the street or avenue", example = "Pobediteley Avenue")
        @NotBlank(message = "street is required")
        @Size(max = 150, message = "street must not exceed {max} characters")
        String street,

        @Schema(description = "City where the hotel is located", example = "Minsk")
        @NotBlank(message = "city is required")
        @Size(max = 150, message = "city must not exceed {max} characters")
        String city,

        @Schema(description = "Country where the hotel is located", example = "Belarus")
        @NotBlank(message = "country is required")
        @Size(max = 150, message = "country must not exceed {max} characters")
        String country,

        @Schema(description = "Postal or ZIP code", example = "220004")
        @NotBlank(message = "postCode is required")
        @Size(max = 20, message = "postCode must not exceed {max} characters")
        String postCode
) {
}

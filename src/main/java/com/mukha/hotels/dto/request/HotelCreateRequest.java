package com.mukha.hotels.dto.request;

import com.mukha.hotels.dto.AddressDto;
import com.mukha.hotels.dto.ArrivalTimeDto;
import com.mukha.hotels.dto.ContactsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request schema for registering a new hotel property")
public record HotelCreateRequest(

        @Schema(description = "Name of the hotel", example = "DoubleTree by Hilton Minsk")
        @NotBlank(message = "Hotel name cannot be empty")
        @Size(max = 255, message = "Hotel name must not exceed {max} characters")
        String name,

        @Schema(description = "Detailed overview of the hotel",
                example = "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms in the Belorussian capital and stunning views of Minsk city from the hotel's 20th floor ...",
                nullable = true)
        @Size(max = 3000, message = "Description must not exceed {max} characters")
        String description,

        @Schema(description = "The trade brand affiliation or franchise network", example = "Hilton")
        @Size(max = 100, message = "Brand name must not exceed {max} characters")
        @NotBlank(message = "Hotel brand cannot be empty")
        String brand,

        @Schema(description = "Structured geopolitical and physical address location data")
        @NotNull(message = "Address details are required")
        @Valid
        AddressDto address,

        @Schema(description = "Hotel communication contacts (phone and email)")
        @Valid
        ContactsDto contacts,

        @Schema(description = "Standard operating hours constraint for guest processing")
        @Valid
        @NotNull(message = "Arrival time are required")
        ArrivalTimeDto arrivalTime
) {
}

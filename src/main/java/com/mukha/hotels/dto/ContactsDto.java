package com.mukha.hotels.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ContactsDto(

        @Schema(example = "+375 17 309-80-00")
        @Size(max = 50, message = "Phone number must not exceed {max} characters")
        String phone,

        @Schema(example = "doubletreeminsk.info@hilton.com")
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must not exceed {max} characters")
        String email) {
}

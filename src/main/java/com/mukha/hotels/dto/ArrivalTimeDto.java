package com.mukha.hotels.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ArrivalTimeDto(

        @Schema(type = "string",
                format = "time",
                example = "14:00")
        @NotNull(message = "Check-in time is required")
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkIn,

        @Schema(type = "string",
                format = "time",
                example = "12:00",
                nullable = true)
        @JsonFormat(pattern = "HH:mm")
        LocalTime checkOut) {
}

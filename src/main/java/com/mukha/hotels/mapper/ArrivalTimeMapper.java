package com.mukha.hotels.mapper;

import com.mukha.hotels.dto.ArrivalTimeDto;
import com.mukha.hotels.model.ArrivalTime;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArrivalTimeMapper {

    ArrivalTimeDto toDto(ArrivalTime arrivalTime);

    ArrivalTime toEntity(ArrivalTimeDto arrivalTimeDto);
}

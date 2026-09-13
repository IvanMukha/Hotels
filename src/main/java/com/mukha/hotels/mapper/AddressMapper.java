package com.mukha.hotels.mapper;

import com.mukha.hotels.dto.AddressDto;
import com.mukha.hotels.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {

    AddressDto toDto(Address address);

    Address toEntity(AddressDto addressDto);
}

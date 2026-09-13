package com.mukha.hotels.mapper;

import com.mukha.hotels.dto.ContactsDto;
import com.mukha.hotels.model.Contacts;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContactsMapper {

    ContactsDto toDto(Contacts contacts);

    Contacts toEntity(ContactsDto contactsDto);
}

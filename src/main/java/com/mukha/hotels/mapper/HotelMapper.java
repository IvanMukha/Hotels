package com.mukha.hotels.mapper;

import com.mukha.hotels.dto.request.HotelCreateRequest;
import com.mukha.hotels.dto.response.HotelDetailResponse;
import com.mukha.hotels.dto.response.HotelShortResponse;
import com.mukha.hotels.model.Address;
import com.mukha.hotels.model.Amenity;
import com.mukha.hotels.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AddressMapper.class, ArrivalTimeMapper.class, ContactsMapper.class,}
)
public interface HotelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    Hotel toEntity(HotelCreateRequest request);

    HotelDetailResponse toDetailResponse(Hotel hotel);

    @Mapping(target = "phone", source = "contacts.phone")
    HotelShortResponse toShortResponse(Hotel hotel);

    default String mapAddressToString(Address address) {
        if (address == null) {
            return null;
        }
        return String.format("%s %s, %s, %s, %s",
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getPostCode(),
                address.getCountry()
        ).trim().replaceAll("^,\\s*|,\\s*$", "");
    }

    default String mapAmenityToString(Amenity amenity) {
        if (amenity == null) {
            return null;
        }
        return amenity.getName();
    }

}

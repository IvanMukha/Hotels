package com.mukha.hotels.unittests;

import com.mukha.hotels.dto.AddressDto;
import com.mukha.hotels.dto.ArrivalTimeDto;
import com.mukha.hotels.dto.ContactsDto;
import com.mukha.hotels.dto.request.HotelCreateRequest;
import com.mukha.hotels.dto.response.HotelDetailResponse;
import com.mukha.hotels.dto.response.HotelShortResponse;
import com.mukha.hotels.exception.HotelNotFoundException;
import com.mukha.hotels.exception.UnknownHistogramParameterException;
import com.mukha.hotels.mapper.HotelMapper;
import com.mukha.hotels.model.Address;
import com.mukha.hotels.model.Amenity;
import com.mukha.hotels.model.ArrivalTime;
import com.mukha.hotels.model.Contacts;
import com.mukha.hotels.model.Hotel;
import com.mukha.hotels.repository.AmenityRepository;
import com.mukha.hotels.repository.HotelRepository;
import com.mukha.hotels.service.impl.HotelServiceImpl;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelMapper hotelMapper;

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private HotelServiceImpl hotelServiceImpl;

    private static final Long HOTEL_ID = 1L;
    private static final String HOTEL_NAME = "DoubleTree by Hilton Minsk";
    private static final String HOTEL_NAME2 = "Marriott";
    private static final String HOTEL_DESCRIPTION = "Description of Hotel";
    private static final String HOTEL_BRAND = "Hilton";

    private static final String ADDRESS_HOUSE_NUMBER = "9";
    private static final String ADDRESS_STREET = "Pobediteley Avenue";
    private static final String ADDRESS_CITY = "Minsk";
    private static final String ADDRESS_COUNTRY = "Belarus";
    private static final String ADDRESS_POSTCODE = "220004";
    private static final String FULL_ADDRESS_STRING = "9 Pobediteley Avenue, Minsk, 220004, Belarus";

    private static final String CONTACT_PHONE = "+375 17 309-80-00";
    private static final String CONTACT_EMAIL = "doubletreeminsk.info@hilton.com";

    private static final LocalTime CHECK_IN = LocalTime.of(12, 0);
    private static final LocalTime CHECK_OUT = LocalTime.of(12, 0);

    private static final String AMENITY1 = "Wi-Fi";
    private static final String AMENITY2 = "Pool";

    private static final String PARAM = "brand";
    private static final String INVALID_PARAM = "invalid_param";


    private Hotel hotel;
    private HotelCreateRequest hotelCreateRequest;
    private HotelDetailResponse hotelDetailResponse;
    private HotelShortResponse hotelShortResponse;

    @BeforeEach
    void setUp() {
        Address address = new Address();
        address.setHouseNumber(ADDRESS_HOUSE_NUMBER);
        address.setStreet(ADDRESS_STREET);
        address.setCity(ADDRESS_CITY);
        address.setCountry(ADDRESS_COUNTRY);
        address.setPostCode(ADDRESS_POSTCODE);

        Contacts contacts = new Contacts();
        contacts.setPhone(CONTACT_PHONE);
        contacts.setEmail(CONTACT_EMAIL);

        ArrivalTime arrivalTime = new ArrivalTime();
        arrivalTime.setCheckIn(CHECK_IN);
        arrivalTime.setCheckOut(CHECK_OUT);

        hotel = new Hotel();
        hotel.setId(HOTEL_ID);
        hotel.setName(HOTEL_NAME);
        hotel.setDescription(HOTEL_DESCRIPTION);
        hotel.setBrand(HOTEL_BRAND);
        hotel.setAddress(address);
        hotel.setContacts(contacts);
        hotel.setArrivalTime(arrivalTime);

        AddressDto addressDto = new AddressDto(ADDRESS_HOUSE_NUMBER, ADDRESS_STREET, ADDRESS_CITY, ADDRESS_COUNTRY, ADDRESS_POSTCODE);
        ContactsDto contactsDto = new ContactsDto(CONTACT_PHONE, CONTACT_EMAIL);
        ArrivalTimeDto arrivalTimeDto = new ArrivalTimeDto(CHECK_IN, CHECK_OUT);

        hotelCreateRequest = new HotelCreateRequest(HOTEL_NAME, HOTEL_DESCRIPTION, HOTEL_BRAND, addressDto, contactsDto, arrivalTimeDto);
        hotelShortResponse = new HotelShortResponse(HOTEL_ID, HOTEL_NAME, HOTEL_DESCRIPTION, FULL_ADDRESS_STRING, CONTACT_PHONE);
        hotelDetailResponse = new HotelDetailResponse(HOTEL_ID, HOTEL_NAME, HOTEL_DESCRIPTION, HOTEL_BRAND, addressDto, contactsDto, arrivalTimeDto, new HashSet<>());

    }

    @Test
    void createHotel_shouldCreateHotelSuccessfully() {
        when(hotelMapper.toEntity(hotelCreateRequest)).thenReturn(hotel);
        when(hotelRepository.save(hotel)).thenReturn(hotel);
        when(hotelMapper.toShortResponse(hotel)).thenReturn(hotelShortResponse);

        HotelShortResponse result = hotelServiceImpl.createHotel(hotelCreateRequest);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(HOTEL_ID);
        assertThat(result.name()).isEqualTo(HOTEL_NAME);

        verify(hotelMapper).toEntity(hotelCreateRequest);
        verify(hotelRepository).save(hotel);
        verify(hotelMapper).toShortResponse(hotel);
    }

    @Test
    void getById_shouldReturnHotel_whenExists() {
        when(hotelRepository.findById(HOTEL_ID)).thenReturn(Optional.of(hotel));
        when(hotelMapper.toDetailResponse(hotel)).thenReturn(hotelDetailResponse);

        HotelDetailResponse result = hotelServiceImpl.getById(HOTEL_ID);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(HOTEL_ID);
        assertThat(result.name()).isEqualTo(HOTEL_NAME);

        verify(hotelRepository).findById(HOTEL_ID);
        verify(hotelMapper).toDetailResponse(hotel);
    }

    @Test
    void getById_shouldThrowHotelNotFoundException_WhenNotExists() {
        when(hotelRepository.findById(HOTEL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelServiceImpl.getById(HOTEL_ID))
                .isInstanceOf(HotelNotFoundException.class);

        verify(hotelRepository).findById(HOTEL_ID);
        verify(hotelMapper, never()).toDetailResponse(any());
    }

    @Test
    void getAll_shouldReturnEmptyPage_whenNoHotelsFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(hotelRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

        Page<HotelShortResponse> result = hotelServiceImpl.getAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();

        verify(hotelRepository).findAll(pageable);
    }

    @Test
    void getAll_shouldReturnPageOfHotels_whenFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel), pageable, 1);

        when(hotelRepository.findAll(pageable)).thenReturn(hotelPage);
        when(hotelMapper.toShortResponse(hotel)).thenReturn(hotelShortResponse);

        Page<HotelShortResponse> result = hotelServiceImpl.getAll(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo(HOTEL_NAME);

        verify(hotelRepository).findAll(pageable);
        verify(hotelMapper).toShortResponse(hotel);
    }

    @Test
    void getAllFiltered_shouldReturnPageOfHotels_whenFiltersApplied() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Hotel> hotelPage = new PageImpl<>(List.of(hotel), pageable, 1);
        List<String> amenities = List.of(AMENITY1, AMENITY2);

        when(hotelRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(hotelPage);
        when(hotelMapper.toShortResponse(hotel)).thenReturn(hotelShortResponse);

        Page<HotelShortResponse> result = hotelServiceImpl.getAllFiltered(
                HOTEL_NAME, HOTEL_BRAND, ADDRESS_CITY, ADDRESS_COUNTRY, amenities, pageable
        );

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo(HOTEL_NAME);

        verify(hotelRepository).findAll(any(Specification.class), eq(pageable));
        verify(hotelMapper).toShortResponse(hotel);
    }

    @Test
    void addAmenitiesToHotel_shouldAddExistingAndCreateNewAmenities() {
        hotel.setAmenities(new HashSet<>());
        when(hotelRepository.findById(HOTEL_ID)).thenReturn(Optional.of(hotel));

        Amenity existingAmenity = new Amenity();
        existingAmenity.setName(AMENITY1);

        when(amenityRepository.findByName(AMENITY1)).thenReturn(Optional.of(existingAmenity));
        when(amenityRepository.findByName(AMENITY2)).thenReturn(Optional.empty());

        when(amenityRepository.save(any(Amenity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(hotelRepository.save(hotel)).thenReturn(hotel);

        hotelServiceImpl.addAmenitiesToHotel(HOTEL_ID, List.of(AMENITY1, AMENITY2));

        assertThat(hotel.getAmenities()).hasSize(2);
        assertThat(hotel.getAmenities()).extracting(Amenity::getName).containsExactlyInAnyOrder(AMENITY1, AMENITY2);

        verify(amenityRepository).findByName(AMENITY1);
        verify(amenityRepository).findByName(AMENITY2);
        verify(amenityRepository).save(any(Amenity.class));
        verify(hotelRepository).save(hotel);
    }

    @Test
    void getHistogram_shouldReturnBrandStats_whenParamIsBrand() {
        Tuple brandTuple1 = mockTuple(HOTEL_NAME, 5L);
        Tuple brandTuple2 = mockTuple(HOTEL_NAME2, 3L);
        when(hotelRepository.countHotelsByBrand()).thenReturn(List.of(brandTuple1, brandTuple2));

        Map<String, Long> result = hotelServiceImpl.getHistogram(PARAM);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(HOTEL_NAME)).isEqualTo(5L);
        assertThat(result.get(HOTEL_NAME2)).isEqualTo(3L);

        verify(hotelRepository).countHotelsByBrand();
    }

    @Test
    void getHistogram_shouldThrowUnknownHistogramParameterException_whenParamIsInvalid() {

        assertThatThrownBy(() -> hotelServiceImpl.getHistogram(INVALID_PARAM))
                .isInstanceOf(UnknownHistogramParameterException.class)
                .hasMessageContaining(INVALID_PARAM);

        verify(hotelRepository, never()).countHotelsByBrand();
        verify(hotelRepository, never()).countHotelsByCity();
    }

    private Tuple mockTuple(String name, Long count) {
        Tuple tuple = org.mockito.Mockito.mock(Tuple.class);
        when(tuple.get("name", String.class)).thenReturn(name);
        when(tuple.get("count", Long.class)).thenReturn(count);
        return tuple;
    }
}

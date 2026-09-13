package com.mukha.hotels.integrationtests;

import com.mukha.hotels.dto.AddressDto;
import com.mukha.hotels.dto.ArrivalTimeDto;
import com.mukha.hotels.dto.ContactsDto;
import com.mukha.hotels.dto.request.HotelCreateRequest;
import com.mukha.hotels.model.Address;
import com.mukha.hotels.model.Amenity;
import com.mukha.hotels.model.ArrivalTime;
import com.mukha.hotels.model.Contacts;
import com.mukha.hotels.model.Hotel;
import com.mukha.hotels.repository.AmenityRepository;
import com.mukha.hotels.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HotelControllerTest {

    private static final String BASE_URL = "/property-view";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @BeforeEach
    void cleanState() {
        hotelRepository.deleteAll();
        amenityRepository.deleteAll();
    }

    @Test
    void createHotel_shouldReturn201AndShortResponse() throws Exception {
        HotelCreateRequest request = validCreateRequest();

        mockMvc.perform(post(BASE_URL + "/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("DoubleTree by Hilton Minsk"))
                .andExpect(jsonPath("$.phone").value("+375 17 309-80-00"))
                .andExpect(jsonPath("$.address").value("9 Pobediteley Avenue, Minsk, 220004, Belarus"));

        assertThat_singleHotelPersisted();
    }

    @Test
    void createHotel_shouldReturn400_whenNameBlank() throws Exception {
        HotelCreateRequest request = new HotelCreateRequest(
                " ",
                null,
                "Hilton",
                new AddressDto("9", "Pobediteley Avenue", "Minsk", "Belarus", "220004"),
                new ContactsDto("+375 17 309-80-00", "info@hilton.com"),
                new ArrivalTimeDto(LocalTime.of(14, 0), LocalTime.of(12, 0))
        );

        mockMvc.perform(post(BASE_URL + "/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid_fields.name").exists());
    }

    @Test
    void createHotel_shouldReturn400_whenAddressMissing() throws Exception {
        HotelCreateRequest request = new HotelCreateRequest(
                "Some Hotel", null, "Hilton",
                null,
                new ContactsDto("+375 17 309-80-00", "info@hilton.com"),
                new ArrivalTimeDto(LocalTime.of(14, 0), LocalTime.of(12, 0))
        );

        mockMvc.perform(post(BASE_URL + "/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid_fields.address").exists());
    }

    @Test
    void getById_shouldReturnDetailResponse_whenExists() throws Exception {
        Amenity wifi = amenityRepository.save(newAmenity("Free WiFi"));
        Amenity parking = amenityRepository.save(newAmenity("Free parking"));

        Hotel hotel = hotelRepository.save(newHotel("DoubleTree by Hilton Minsk", "Hilton",
                "Minsk", "Belarus", Set.of(wifi, parking)));

        mockMvc.perform(get(BASE_URL + "/hotels/{id}", hotel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(hotel.getId()))
                .andExpect(jsonPath("$.brand").value("Hilton"))
                .andExpect(jsonPath("$.address.city").value("Minsk"))
                .andExpect(jsonPath("$.address.country").value("Belarus"))
                .andExpect(jsonPath("$.contacts.phone").value("+375 17 309-80-00"))
                .andExpect(jsonPath("$.arrivalTime.checkIn").value("14:00"))
                .andExpect(jsonPath("$.arrivalTime.checkOut").value("12:00"))
                .andExpect(jsonPath("$.amenities", hasSize(2)))
                .andExpect(jsonPath("$.amenities", containsInAnyOrder("Free WiFi", "Free parking")));
    }

    @Test
    void getById_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get(BASE_URL + "/hotels/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Hotel with id: 999999 not found"));
    }

    @Test
    void getAll_shouldReturnFlatArrayOfShortResponses() throws Exception {
        hotelRepository.save(newHotel("Hotel 1", "Hilton", "Minsk", "Belarus", Set.of()));
        hotelRepository.save(newHotel("Hotel 2", "Marriott", "Moscow", "Russia", Set.of()));

        mockMvc.perform(get(BASE_URL + "/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].address").exists())
                .andExpect(jsonPath("$[0].phone").exists());
    }

    @Test
    void search_shouldFilterByCity() throws Exception {
        hotelRepository.save(newHotel("Hotel 1", "Hilton", "Minsk", "Belarus", Set.of()));
        hotelRepository.save(newHotel("Hotel 2", "Marriott", "Moscow", "Russia", Set.of()));

        mockMvc.perform(get(BASE_URL + "/search").param("city", "minsk"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Hotel 1"));
    }

    @Test
    void search_shouldFilterByAmenities_requiringAllOfThem() throws Exception {
        Amenity wifi = amenityRepository.save(newAmenity("Free WiFi"));
        Amenity pool = amenityRepository.save(newAmenity("Pool"));

        hotelRepository.save(newHotel("Hotel WifiOnly", "Hilton", "Minsk", "Belarus", Set.of(wifi)));
        hotelRepository.save(newHotel("Hotel WifiAndPool", "Marriott", "Minsk", "Belarus", Set.of(wifi, pool)));

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("amenities", "Free WiFi", "Pool"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Hotel WifiAndPool"));
    }

    @Test
    void search_shouldReturnEmptyArray_whenNoMatches() throws Exception {
        hotelRepository.save(newHotel("Hotel 1", "Hilton", "Minsk", "Belarus", Set.of()));

        mockMvc.perform(get(BASE_URL + "/search").param("city", "Tokyo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void addAmenitiesToHotel_shouldAttachExistingAndNewAmenities() throws Exception {
        Amenity wifi = amenityRepository.save(newAmenity("Free WiFi"));
        Hotel hotel = hotelRepository.save(newHotel("Hotel 1", "Hilton", "Minsk", "Belarus", Set.of()));

        mockMvc.perform(post(BASE_URL + "/hotels/{id}/amenities", hotel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("Free WiFi", "Pet-friendly rooms"))))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_URL + "/hotels/{id}", hotel.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amenities", hasSize(2)))
                .andExpect(jsonPath("$.amenities", containsInAnyOrder("Free WiFi", "Pet-friendly rooms")));
    }

    @Test
    void addAmenitiesToHotel_shouldReturn404_whenHotelNotExists() throws Exception {
        mockMvc.perform(post(BASE_URL + "/hotels/{id}/amenities", 999_999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("Free WiFi"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void histogram_shouldGroupByBrand() throws Exception {
        hotelRepository.save(newHotel("Hotel 1", "Hilton", "Minsk", "Belarus", Set.of()));
        hotelRepository.save(newHotel("Hotel 2", "Hilton", "Moscow", "Russia", Set.of()));
        hotelRepository.save(newHotel("Hotel 3", "Marriott", "Warszawa", "Poland", Set.of()));

        mockMvc.perform(get(BASE_URL + "/histogram/{param}", "brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Hilton").value(2))
                .andExpect(jsonPath("$.Marriott").value(1));
    }

    @Test
    void histogram_shouldGroupByAmenities() throws Exception {
        Amenity wifi = amenityRepository.save(newAmenity("Free WiFi"));
        Amenity pool = amenityRepository.save(newAmenity("Pool"));

        hotelRepository.save(newHotel("Hotel 1", "Hilton", "Minsk", "Belarus", Set.of(wifi)));
        hotelRepository.save(newHotel("Hotel 2", "Marriott", "Moscow", "Russia", Set.of(wifi, pool)));

        mockMvc.perform(get(BASE_URL + "/histogram/{param}", "amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['Free WiFi']").value(2))
                .andExpect(jsonPath("$.Pool").value(1));
    }

    @Test
    void histogram_shouldReturn400_forUnknownParameter() throws Exception {
        mockMvc.perform(get(BASE_URL + "/histogram/{param}", "unknown_param"))
                .andExpect(status().isBadRequest());
    }

    private HotelCreateRequest validCreateRequest() {
        return new HotelCreateRequest(
                "DoubleTree by Hilton Minsk",
                "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...",
                "Hilton",
                new AddressDto("9", "Pobediteley Avenue", "Minsk", "Belarus", "220004"),
                new ContactsDto("+375 17 309-80-00", "doubletreeminsk.info@hilton.com"),
                new ArrivalTimeDto(LocalTime.of(14, 0), LocalTime.of(12, 0))
        );
    }

    private Hotel newHotel(String name, String brand, String city, String country, Set<Amenity> amenities) {
        Address address = new Address();
        address.setHouseNumber("9");
        address.setStreet("Pobediteley Avenue");
        address.setCity(city);
        address.setCountry(country);
        address.setPostcode("220004");

        Contacts contacts = new Contacts();
        contacts.setPhone("+375 17 309-80-00");
        contacts.setEmail("info@example.com");

        ArrivalTime arrivalTime = new ArrivalTime();
        arrivalTime.setCheckIn(LocalTime.of(14, 0));
        arrivalTime.setCheckOut(LocalTime.of(12, 0));

        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setBrand(brand);
        hotel.setAddress(address);
        hotel.setContacts(contacts);
        hotel.setArrivalTime(arrivalTime);
        hotel.setAmenities(new HashSet<>(amenities));
        return hotel;
    }

    private Amenity newAmenity(String name) {
        Amenity amenity = new Amenity();
        amenity.setName(name);
        return amenity;
    }

    private void assertThat_singleHotelPersisted() {
        List<Hotel> all = hotelRepository.findAll();
        org.assertj.core.api.Assertions.assertThat(all).hasSize(1);
        org.assertj.core.api.Assertions.assertThat(all.getFirst().getName()).isEqualTo("DoubleTree by Hilton Minsk");
    }
}

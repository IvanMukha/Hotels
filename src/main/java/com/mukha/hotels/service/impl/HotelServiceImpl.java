package com.mukha.hotels.service.impl;

import com.mukha.hotels.dto.request.HotelCreateRequest;
import com.mukha.hotels.dto.response.HotelDetailResponse;
import com.mukha.hotels.dto.response.HotelShortResponse;
import com.mukha.hotels.exception.HotelNotFoundException;
import com.mukha.hotels.exception.UnknownHistogramParameterException;
import com.mukha.hotels.mapper.HotelMapper;
import com.mukha.hotels.model.Amenity;
import com.mukha.hotels.model.Hotel;
import com.mukha.hotels.repository.AmenityRepository;
import com.mukha.hotels.repository.HotelRepository;
import com.mukha.hotels.service.HotelService;
import com.mukha.hotels.specification.HotelSpecification;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final AmenityRepository amenityRepository;

    @Transactional
    public HotelShortResponse createHotel(HotelCreateRequest hotelCreateRequest) {
        log.debug("Creating new hotel with name: {} ", hotelCreateRequest.name());
        Hotel createdHotel = hotelRepository.save(hotelMapper.toEntity(hotelCreateRequest));
        log.debug("Successfully create hotel with id: {}", createdHotel.getId());
        return hotelMapper.toShortResponse(createdHotel);
    }

    public HotelDetailResponse getById(Long id) {
        return hotelMapper.toDetailResponse(getHotelEntityById(id));
    }

    public Page<HotelShortResponse> getAll(Pageable pageable) {
        log.debug("Fetching pageable hotels.");
        Page<Hotel> foundHotels = hotelRepository.findAll(pageable);
        return foundHotels.map(hotelMapper::toShortResponse);
    }

    public Page<HotelShortResponse> getAllFiltered(String name, String brand, String city,
                                                   String country, List<String> amenities,
                                                   Pageable pageable) {
        log.debug("Fetching pageable hotels. Filters - name: {}, brand: {}, city: {}, country: {}, amenities: {}", name, brand, city, country, amenities);
        Specification<Hotel> spec = Specification.where(HotelSpecification.hasName(name))
                .and(HotelSpecification.hasBrand(brand))
                .and(HotelSpecification.hasCity(city))
                .and(HotelSpecification.hasCountry(country))
                .and(HotelSpecification.hasAmenities(amenities));
        Page<Hotel> foundHotels = hotelRepository.findAll(spec, pageable);
        return foundHotels.map(hotelMapper::toShortResponse);
    }

    @Transactional
    public void addAmenitiesToHotel(Long hotelId, List<String> amenities) {
        Hotel foundHotel = getHotelEntityById(hotelId);
        log.debug("Adding amenities to hotel: {}", amenities);

        Set<Amenity> currentAmenities = foundHotel.getAmenities();
        if (currentAmenities == null) {
            currentAmenities = new java.util.HashSet<>();
        }

        for (String name : amenities) {
            Amenity amenity = amenityRepository.findByName(name)
                    .orElseGet(() -> {
                        Amenity newAmenity = new Amenity();
                        newAmenity.setName(name);
                        return amenityRepository.save(newAmenity);
                    });
            currentAmenities.add(amenity);
        }

        foundHotel.setAmenities(currentAmenities);
        hotelRepository.save(foundHotel);
    }

    public Map<String, Long> getHistogram(String param) {
        log.debug("Generating histogram for parameter: {}", param);

        List<Tuple> tuples = switch (param.toLowerCase()) {
            case "brand" -> hotelRepository.countHotelsByBrand();
            case "city" -> hotelRepository.countHotelsByCity();
            case "country" -> hotelRepository.countHotelsByCountry();
            case "amenities" -> hotelRepository.countHotelsByAmenities();
            default -> throw new UnknownHistogramParameterException(param);
        };

        return tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get("name", String.class),
                        tuple -> tuple.get("count", Long.class),
                        Long::sum
                ));
    }

    private Hotel getHotelEntityById(Long id) {
        log.debug("Fetching Hotel by id: {}", id);
        Hotel foundHotel = hotelRepository.findById(id).orElseThrow(() -> {
            log.warn("Hotel with id: {} not found", id);
            return new HotelNotFoundException(id);
        });
        return foundHotel;
    }
}

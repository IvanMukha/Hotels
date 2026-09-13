package com.mukha.hotels.service;

import com.mukha.hotels.dto.request.HotelCreateRequest;
import com.mukha.hotels.dto.response.HotelDetailResponse;
import com.mukha.hotels.dto.response.HotelShortResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface HotelService {

    HotelShortResponse createHotel(HotelCreateRequest hotelCreateRequest);

    HotelDetailResponse getById(Long id);

    Page<HotelShortResponse> getAll(Pageable pageable);

    Page<HotelShortResponse> getAllFiltered(String name, String brand, String city,
                                            String country, List<String> amenities,
                                            Pageable pageable);

    void addAmenitiesToHotel(Long hotelId, List<String> amenities);

    Map<String, Long> getHistogram(String param);
}

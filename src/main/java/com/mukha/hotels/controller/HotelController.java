package com.mukha.hotels.controller;

import com.mukha.hotels.dto.request.HotelCreateRequest;
import com.mukha.hotels.dto.response.HotelDetailResponse;
import com.mukha.hotels.dto.response.HotelShortResponse;
import com.mukha.hotels.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/property-view")
@Tag(name = "Hotel controller", description = "Managing hotels, filtering, and statistical histograms")
public class HotelController {
    private final HotelService hotelService;

    @PostMapping("/hotels")
    @Operation(summary = "Create a new hotel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hotel successfully created"),
            @ApiResponse(responseCode = "400", description = "Request validation failed")
    })
    public ResponseEntity<HotelShortResponse> createHotel(@Valid @RequestBody HotelCreateRequest hotelCreateRequest) {
        HotelShortResponse createdHotel = hotelService.createHotel(hotelCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHotel);
    }

    @GetMapping("/hotels/{id}")
    @Operation(summary = "Get detailed information by hotel ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hotel successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<HotelDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getById(id));
    }

    @GetMapping("/hotels")
    @Operation(summary = "Get a paginated list of all hotels")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list")
    })
    public ResponseEntity<List<HotelShortResponse>> getAll(@ParameterObject
                                                           @PageableDefault(page = 0, size = 30, sort = "name")
                                                           Pageable pageable) {
        Page<HotelShortResponse> foundHotels = hotelService.getAll(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(foundHotels.getContent());
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter hotels dynamically")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved matched filtered list")
    })
    public ResponseEntity<List<HotelShortResponse>> getAllFiltered(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) String brand,
                                                                   @RequestParam(required = false) String city,
                                                                   @RequestParam(required = false) String country,
                                                                   @RequestParam(required = false) List<String> amenities,
                                                                   @ParameterObject
                                                                   @PageableDefault(page = 0, size = 30, sort = "name")
                                                                   Pageable pageable) {
        Page<HotelShortResponse> foundHotels = hotelService.getAllFiltered(name, brand, city, country, amenities, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(foundHotels.getContent());
    }

    @PostMapping("/hotels/{id}/amenities")
    @Operation(summary = "Add amenities to a hotel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Amenities successfully assigned to the hotel"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<Void> addAmenitiesToHotel(@PathVariable Long id,
                                                    @RequestBody List<String> amenities) {
        hotelService.addAmenitiesToHotel(id, amenities);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/histogram/{param}")
    @Operation(summary = "Generate localized parameter-driven histogram metrics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistical histogram successfully computed"),
            @ApiResponse(responseCode = "400", description = "Invalid clustering parameter requested")
    })
    public ResponseEntity<Map<String, Long>> getHistogram(@PathVariable String param) {
        return ResponseEntity.status(HttpStatus.OK).body(hotelService.getHistogram(param));
    }
}

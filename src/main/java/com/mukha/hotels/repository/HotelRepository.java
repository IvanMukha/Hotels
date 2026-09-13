package com.mukha.hotels.repository;

import com.mukha.hotels.model.Hotel;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

    @Query("SELECT h.brand AS name, COUNT(h) AS count FROM Hotel h WHERE h.brand IS NOT NULL GROUP BY h.brand")
    List<Tuple> countHotelsByBrand();

    @Query("SELECT h.address.city AS name, COUNT(h) AS count FROM Hotel h WHERE h.address.city IS NOT NULL GROUP BY h.address.city")
    List<Tuple> countHotelsByCity();

    @Query("SELECT h.address.country AS name, COUNT(h) AS count FROM Hotel h WHERE h.address.country IS NOT NULL GROUP BY h.address.country")
    List<Tuple> countHotelsByCountry();

    @Query("SELECT a.name AS name, COUNT(h) AS count FROM Hotel h JOIN h.amenities a GROUP BY a.id, a.name")
    List<Tuple> countHotelsByAmenities();
}

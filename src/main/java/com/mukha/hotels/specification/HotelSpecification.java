package com.mukha.hotels.specification;

import com.mukha.hotels.model.Address;
import com.mukha.hotels.model.Amenity;
import com.mukha.hotels.model.Hotel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@UtilityClass
public class HotelSpecification {

    public static Specification<Hotel> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Hotel.Fields.name)),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Hotel> hasBrand(String brand) {
        return (root, query, criteriaBuilder) -> {
            if (brand == null || brand.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Hotel.Fields.brand)),
                    "%" + brand.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Hotel> hasCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (city == null || city.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Hotel.Fields.address).get(Address.Fields.city)),
                    "%" + city.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Hotel> hasCountry(String country) {
        return (root, query, criteriaBuilder) -> {
            if (country == null || country.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Hotel.Fields.address).get(Address.Fields.country)),
                    "%" + country.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Hotel> hasAmenities(List<String> amenities) {
        return (root, query, criteriaBuilder) -> {
            if (amenities == null || amenities.isEmpty()) {
                return null;
            }

            if (query.getResultType() != Long.class) {
                query.distinct(true);
            }

            Predicate[] predicates = amenities.stream()
                    .filter(name -> name != null && !name.isBlank())
                    .map(name -> {
                        Join<Hotel, Amenity> amenityJoin = root.join(Hotel.Fields.amenities);
                        return criteriaBuilder.like(
                                criteriaBuilder.lower(amenityJoin.get(Amenity.Fields.name)),
                                name.toLowerCase()
                        );
                    })
                    .toArray(Predicate[]::new);

            return criteriaBuilder.and(predicates);
        };
    }
}
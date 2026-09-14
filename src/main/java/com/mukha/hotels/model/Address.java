package com.mukha.hotels.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Embeddable
@Getter
@Setter
@FieldNameConstants
public class Address {

    @Column(name = "address_house_number", length = 50)
    private String houseNumber;

    @Column(name = "address_street", length = 150)
    private String street;

    @Column(name = "address_city", length = 150)
    private String city;

    @Column(name = "address_country", length = 150)
    private String country;

    @Column(name = "address_postcode", length = 20)
    private String postCode;
}

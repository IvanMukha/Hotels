package com.mukha.hotels.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Contacts {

    @Column(name = "contact_phone", length = 50)
    private String phone;

    @Column(name = "contact_email", length = 100)
    private String email;


}

package com.mukha.hotels.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Set;

@Entity
@Table(name = "hotel")
@FieldNameConstants
@Getter
@Setter
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hotel_seq_generator")
    @SequenceGenerator(name = "hotel_seq_generator", sequenceName = "hotel_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name="description")
    private String description;

    @Column(name = "brand")
    private String brand;

    @Embedded
    private Address address;

    @Embedded
    private Contacts contacts;

    @Embedded
    private ArrivalTime arrivalTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hotel_amenity",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities;
}

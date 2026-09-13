package com.mukha.hotels.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "amenity")
@Getter
@Setter
@FieldNameConstants
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "amenity_seq_generator")
    @SequenceGenerator(name = "amenity_seq_generator", sequenceName = "amenity_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
}

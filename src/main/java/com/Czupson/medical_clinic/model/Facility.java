package com.Czupson.medical_clinic.model;

import com.Czupson.medical_clinic.exception.facility.FacilityDataValidationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "facilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String buildingNumber;

    @ManyToMany(mappedBy = "facilities")
    private Set<Doctor> doctors = new HashSet<>();

    public void update(Facility updatedFacility) {
        updatedFacility.validate();
        this.name = updatedFacility.getName();
        this.city = updatedFacility.getCity();
        this.postalCode = updatedFacility.getPostalCode();
        this.street = updatedFacility.getStreet();
        this.buildingNumber = updatedFacility.getBuildingNumber();
    }

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new FacilityDataValidationException("Facility name cannot be empty");
        }
        if (city == null || city.isBlank()) {
            throw new FacilityDataValidationException("City cannot be empty");
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new FacilityDataValidationException("Postal code cannot be empty");
        }
        if (street == null || street.isBlank()) {
            throw new FacilityDataValidationException("Street cannot be empty");
        }
        if (buildingNumber == null || buildingNumber.isBlank()) {
            throw new FacilityDataValidationException("Building number cannot be empty");
        }
    }
}

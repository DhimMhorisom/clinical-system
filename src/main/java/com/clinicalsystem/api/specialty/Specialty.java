package com.clinicalsystem.api.specialty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "specialty")
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    @Setter
    @Column(nullable = false)
    private String description;

    public Specialty(){}
    public Specialty(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

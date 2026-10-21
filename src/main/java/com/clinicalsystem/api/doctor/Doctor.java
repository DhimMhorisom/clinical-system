package com.clinicalsystem.api.doctor;

import com.clinicalsystem.api.specialty.Specialty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "doctor",
uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_doctor_crm_uf",
                columnNames = {"crm" , "crm_uf"}
        )
})
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String crm;

    @Setter
    @Column(name = "crm_uf", nullable = false, length = 2)
    private String crmUf;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialty_id" , nullable = false)
    private Specialty specialty;

    public Doctor(){}
    public Doctor(String name, String crm, String crmUf, Specialty specialty) {
        this.name = name;
        this.crm = crm;
        this.crmUf = crmUf;
        this.specialty = specialty;
    }
}

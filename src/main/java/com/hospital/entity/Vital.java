package com.hospital.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private Double temperature;

    private Double weight;

    private Double height;

    private String bloodPressure;

    private Integer pulse;

    private Double spo2;

    private Integer respiratoryRate;

    @Column(columnDefinition = "TEXT")
    private String nursingNotes;

    private LocalDateTime recordedAt;
}
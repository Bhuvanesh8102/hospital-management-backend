package com.hospital.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    // Doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    // Appointment related to this admission
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    // Bed assigned to patient
    @ManyToOne
    @JoinColumn(name = "bed_id")
    private Bed bed;

    private LocalDate admissionDate;

    private LocalDate dischargeDate;

    private String reason;

    private String status;
}
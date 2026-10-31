package com.hospital.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hospital_bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String billNumber;

    // Patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    // Doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    // Appointment
    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    // Admission
    @ManyToOne
    @JoinColumn(name = "admission_id")
    private Admission admission;

    // Description
    @Column(columnDefinition = "TEXT")
    private String description;

    // Charges
    private Double registrationCharges;

    private Double consultationCharges;

    private Double laboratoryCharges;

    private Double admissionCharges;

    private Double roomCharges;

    private Double otherCharges;

    // Total
    private Double totalAmount;

    // PAID / PENDING
    private String paymentStatus;

    // Date
    private LocalDateTime billDate;
}
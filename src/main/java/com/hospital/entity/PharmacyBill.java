package com.hospital.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pharmacy_bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String billNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    // Medicine name comes from doctor's prescription
    private String medicineName;

    // Price entered manually by pharmacist
    private Double pricePerTablet;

    // Quantity entered manually by pharmacist
    private Integer quantity;

    // pricePerTablet × quantity
    private Double totalAmount;

    private String paymentStatus;

    private String dispensingStatus;

    private LocalDateTime billDate;
}
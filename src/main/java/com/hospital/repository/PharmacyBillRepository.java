package com.hospital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.PharmacyBill;

@Repository
public interface PharmacyBillRepository
        extends JpaRepository<PharmacyBill, Long> {

    Optional<PharmacyBill> findByBillNumber(String billNumber);

    List<PharmacyBill> findByPatientId(Long patientId);

    List<PharmacyBill> findByPrescriptionId(Long prescriptionId);

    List<PharmacyBill> findByPaymentStatus(String paymentStatus);

    List<PharmacyBill> findByDispensingStatus(String dispensingStatus);

    boolean existsByBillNumber(String billNumber);
}	
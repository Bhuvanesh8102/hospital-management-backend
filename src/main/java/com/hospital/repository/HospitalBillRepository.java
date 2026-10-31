package com.hospital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.HospitalBill;

@Repository
public interface HospitalBillRepository
        extends JpaRepository<HospitalBill, Long> {

    Optional<HospitalBill> findByBillNumber(String billNumber);

    List<HospitalBill> findByPatientId(Long patientId);

    List<HospitalBill> findByAdmissionId(Long admissionId);

    List<HospitalBill> findByPaymentStatus(String paymentStatus);

    boolean existsByBillNumber(String billNumber);
}
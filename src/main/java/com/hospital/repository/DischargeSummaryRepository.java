package com.hospital.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.DischargeSummary;

@Repository
public interface DischargeSummaryRepository
        extends JpaRepository<DischargeSummary, Long> {

    Optional<DischargeSummary> findByAdmissionId(Long admissionId);

    Optional<DischargeSummary> findByPatientId(Long patientId);

    boolean existsByAdmissionId(Long admissionId);
}
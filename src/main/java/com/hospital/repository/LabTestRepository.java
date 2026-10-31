package com.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.LabTest;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {

    List<LabTest> findByPatientId(Long patientId);

    List<LabTest> findByDoctorId(Long doctorId);

    List<LabTest> findByAppointmentId(Long appointmentId);

    List<LabTest> findByStatus(String status);

    List<LabTest> findByPatientIdAndStatus(
            Long patientId, String status);

    List<LabTest> findByTestCategory(String testCategory);
}
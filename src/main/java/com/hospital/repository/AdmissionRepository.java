package com.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.Admission;

@Repository
public interface AdmissionRepository
        extends JpaRepository<Admission, Long> {

    List<Admission> findByPatientId(Long patientId);

    List<Admission> findByDoctorId(Long doctorId);

    List<Admission> findByStatus(String status);

    List<Admission> findByPatientIdAndStatus(
            Long patientId,
            String status
    );

    List<Admission> findByBedId(Long bedId);

    // Get admissions belonging to a particular appointment
    List<Admission> findByAppointmentId(Long appointmentId);
}
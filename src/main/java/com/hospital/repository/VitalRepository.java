package com.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.Vital;

@Repository
public interface VitalRepository extends JpaRepository<Vital, Long> {

    List<Vital> findByAppointmentId(Long appointmentId);

    List<Vital> findByPatientId(Long patientId);
}
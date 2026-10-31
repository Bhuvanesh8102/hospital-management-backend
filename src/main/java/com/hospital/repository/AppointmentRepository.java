package com.hospital.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.Appointment;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentId(
            String appointmentId
    );

    boolean existsByAppointmentId(
            String appointmentId
    );

    List<Appointment> findByPatientId(
            Long patientId
    );

    List<Appointment> findByDoctorId(
            Long doctorId
    );

    List<Appointment> findByDoctorIdAndAppointmentDate(
            Long doctorId,
            LocalDate appointmentDate
    );

    List<Appointment> findByPatientIdAndStatus(
            Long patientId,
            String status
    );

    List<Appointment> findByDoctorIdAndStatus(
            Long doctorId,
            String status
    );

    List<Appointment> findByAppointmentDate(
            LocalDate appointmentDate
    );

    List<Appointment> findByStatus(
            String status
    );
    
   
}
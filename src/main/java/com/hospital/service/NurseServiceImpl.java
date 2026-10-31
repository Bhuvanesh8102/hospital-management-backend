package com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospital.entity.Appointment;
import com.hospital.entity.User;
import com.hospital.entity.Vital;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.UserRepository;
import com.hospital.repository.VitalRepository;

@Service
public class NurseServiceImpl implements NurseService {

    private final UserRepository userRepository;
    private final VitalRepository vitalRepository;
    private final AppointmentRepository appointmentRepository;

    public NurseServiceImpl(
            UserRepository userRepository,
            VitalRepository vitalRepository,
            AppointmentRepository appointmentRepository) {

        this.userRepository = userRepository;
        this.vitalRepository = vitalRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // =========================================================
    // VIEW PATIENTS
    // =========================================================

    @Override
    public List<User> getPatients(Long nurseId) {

        // Nurse does not have permanently assigned patients.
        // Therefore, return all registered patients.

        return userRepository.findByRole("PATIENT");
    }


    // =========================================================
    // RECORD PATIENT VITALS
    // =========================================================

    @Override
    public Vital recordVitals(Vital vital) {

        // -----------------------------------------------------
        // Validate patient
        // -----------------------------------------------------

        if (vital.getPatient() == null ||
                vital.getPatient().getId() == null) {

            throw new RuntimeException(
                    "Patient ID is required"
            );
        }


        // -----------------------------------------------------
        // Validate appointment
        // -----------------------------------------------------

        if (vital.getAppointment() == null ||
                vital.getAppointment().getId() == null) {

            throw new RuntimeException(
                    "Appointment ID is required"
            );
        }


        // -----------------------------------------------------
        // Find actual patient from database
        // -----------------------------------------------------

        User patient = userRepository.findById(
                vital.getPatient().getId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Patient not found"
                )
        );


        // -----------------------------------------------------
        // Find actual appointment from database
        // -----------------------------------------------------

        Appointment appointment =
                appointmentRepository.findById(
                        vital.getAppointment().getId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Appointment not found"
                        )
                );


        // -----------------------------------------------------
        // Make sure appointment belongs to selected patient
        // -----------------------------------------------------

        if (appointment.getPatient() == null ||
                appointment.getPatient().getId() == null ||
                !appointment.getPatient().getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Appointment does not belong to this patient"
            );
        }


        // -----------------------------------------------------
        // Set actual entities
        // -----------------------------------------------------

        vital.setPatient(patient);

        vital.setAppointment(appointment);


        // -----------------------------------------------------
        // Set recording time
        // -----------------------------------------------------

        if (vital.getRecordedAt() == null) {

            vital.setRecordedAt(
                    java.time.LocalDateTime.now()
            );
        }


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        return vitalRepository.save(vital);
    }


    // =========================================================
    // VIEW VITALS BY APPOINTMENT
    // =========================================================

    @Override
    public List<Vital> getVitalsByAppointment(
            Long appointmentId) {

        return vitalRepository.findByAppointmentId(
                appointmentId
        );
    }


    // =========================================================
    // VIEW PATIENT'S PREVIOUS VITALS
    // =========================================================

    @Override
    public List<Vital> getPatientVitals(
            Long patientId) {

        return vitalRepository.findByPatientId(
                patientId
        );
    }
    
    @Override
    public List<Appointment> getPatientAppointments(Long patientId) {

        return appointmentRepository.findByPatientId(patientId);
    }
}
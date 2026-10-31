package com.hospital.service;

import java.util.List;

import com.hospital.entity.Appointment;
import com.hospital.entity.User;
import com.hospital.entity.Vital;

public interface NurseService {

    // View patients
    List<User> getPatients(Long nurseId);

    // Record patient vitals
    Vital recordVitals(Vital vital);

    // View vitals for an appointment
    List<Vital> getVitalsByAppointment(Long appointmentId);

    // View patient's previous vitals
    List<Vital> getPatientVitals(Long patientId);
    
    List<Appointment> getPatientAppointments(Long patientId);
}
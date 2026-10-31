package com.hospital.service;

import java.util.List;

import com.hospital.entity.Appointment;
import com.hospital.entity.Consultation;
import com.hospital.entity.LabTest;
import com.hospital.entity.Prescription;
import com.hospital.entity.User;
import com.hospital.entity.Vital;

public interface DoctorService {

    // APPOINTMENTS
    List<Appointment> getDoctorAppointments(Long doctorId);

    List<Appointment> getDoctorAppointmentsByStatus(
            Long doctorId,
            String status
    );


    // PATIENT
    User getPatientById(Long patientId);


    // VITALS
    List<Vital> getPatientVitals(Long patientId);


    // CONSULTATION
    Consultation createConsultation(
            Consultation consultation
    );

    Consultation getConsultationByAppointment(
            Long appointmentId
    );

    List<Consultation> getDoctorConsultations(
            Long doctorId
    );


    // PRESCRIPTION
    Prescription createPrescription(
            Prescription prescription
    );

    List<Prescription> getPatientPrescriptions(
            Long patientId
    );


    // LAB
    LabTest orderLabTest(
            LabTest labTest
    );

    List<LabTest> getPatientLabTests(
            Long patientId
    );


    // RECOMMENDATIONS
    Consultation recommendAdmission(
            Long consultationId
    );

    Consultation recommendOperation(
            Long consultationId
    );

    Consultation recommendDischarge(
            Long consultationId
    );


    // DOCTOR SEARCH
    List<String> getSpecializations();

    List<User> getDoctorsBySpecialization(
            String specialization
    );
    
    User getDoctorById(Long doctorId);
    
    Appointment verifyAppointment(Long appointmentId);
    
    List<Prescription> getPrescriptionsByAppointment(Long appointmentId);
    
    List<Vital> getVitalsByAppointment(Long appointmentId);
}
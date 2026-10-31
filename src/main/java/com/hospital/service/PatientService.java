package com.hospital.service;

import java.util.List;

import com.hospital.entity.Admission;
import com.hospital.entity.Appointment;
import com.hospital.entity.Consultation;
import com.hospital.entity.DischargeSummary;
import com.hospital.entity.HospitalBill;
import com.hospital.entity.LabTest;
import com.hospital.entity.Prescription;
import com.hospital.entity.PharmacyBill;
import com.hospital.entity.User;
import com.hospital.entity.Vital;

public interface PatientService {

    User registerPatient(User patient);

    User getPatientById(Long patientId);

    User getPatientByUhid(String uhid);

    Appointment bookAppointment(Appointment appointment);

    List<Appointment> getPatientAppointments(Long patientId);

    List<Consultation> getPatientConsultations(Long patientId);

    List<Vital> getPatientVitals(Long patientId);

    List<Prescription> getPatientPrescriptions(Long patientId);

    List<LabTest> getPatientLabTests(Long patientId);

    List<Admission> getPatientAdmissions(Long patientId);

    DischargeSummary getDischargeSummary(Long admissionId);

    List<HospitalBill> getHospitalBills(Long patientId);

    List<PharmacyBill> getPharmacyBills(Long patientId);
    
   
}
package com.hospital.service;

import java.util.List;

import com.hospital.entity.Appointment;
import com.hospital.entity.PharmacyBill;
import com.hospital.entity.Prescription;

public interface PharmacistService {

    // =========================
    // Pharmacist Dashboard
    // =========================

    List<Prescription> getAllPrescriptions();

    List<PharmacyBill> getAllPharmacyBills();


    // =========================
    // Patient Prescriptions
    // =========================

    List<Prescription> getPatientPrescriptions(Long patientId);

    List<Appointment> getPatientAppointments(Long patientId);

    List<Prescription> getPrescriptionsByAppointment(Long appointmentId);


    // =========================
    // Pharmacy Billing
    // =========================

    PharmacyBill createPharmacyBill(PharmacyBill pharmacyBill);

    PharmacyBill completePayment(Long billId);


    // =========================
    // Dispensing
    // =========================

    PharmacyBill dispenseMedicine(Long billId);


    // =========================
    // Patient Pharmacy Bills
    // =========================

    List<PharmacyBill> getPatientPharmacyBills(Long patientId);
}
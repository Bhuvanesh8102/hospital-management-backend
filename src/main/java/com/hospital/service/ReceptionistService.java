package com.hospital.service;

import java.util.List;

import com.hospital.entity.Admission;
import com.hospital.entity.Appointment;
import com.hospital.entity.HospitalBill;
import com.hospital.entity.User;

public interface ReceptionistService {

    Appointment getAppointmentByAppointmentId(String appointmentId);

    List<Appointment> getAllAppointments();

    Appointment confirmAppointment(Long appointmentId);

    Appointment cancelAppointment(Long appointmentId);

    Appointment checkInAppointment(Long appointmentId);

    Appointment markNoShow(Long appointmentId);

    Appointment createAppointment(Appointment appointment);

    // NEW: Walk-in patient registration
    User registerWalkInPatient(User user);

    User searchPatientByUhid(String uhid);

    User searchPatientByMobile(String mobile);

    User searchPatientByName(String name);

    Admission createAdmission(Admission admission);

    HospitalBill createHospitalBill(HospitalBill hospitalBill);

    HospitalBill getHospitalBill(Long billId);

    HospitalBill completeHospitalPayment(Long billId);

    HospitalBill completeFinalBilling(Long billId);

    Admission processDischarge(Long admissionId);
    
    List<Admission> getPatientAdmissions(Long patientId);
}
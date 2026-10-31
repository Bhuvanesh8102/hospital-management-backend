package com.hospital.service;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.entity.Admission;
import com.hospital.entity.Appointment;
import com.hospital.entity.Consultation;
import com.hospital.entity.DischargeSummary;
import com.hospital.entity.HospitalBill;
import com.hospital.entity.LabTest;
import com.hospital.entity.PharmacyBill;
import com.hospital.entity.Prescription;
import com.hospital.entity.User;
import com.hospital.entity.Vital;
import com.hospital.repository.AdmissionRepository;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.DischargeSummaryRepository;
import com.hospital.repository.HospitalBillRepository;
import com.hospital.repository.LabTestRepository;
import com.hospital.repository.PharmacyBillRepository;
import com.hospital.repository.PrescriptionRepository;
import com.hospital.repository.UserRepository;
import com.hospital.repository.VitalRepository;

@Service
public class PatientServiceImpl implements PatientService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final VitalRepository vitalRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabTestRepository labTestRepository;
    private final AdmissionRepository admissionRepository;
    private final DischargeSummaryRepository dischargeSummaryRepository;
    private final HospitalBillRepository hospitalBillRepository;
    private final PharmacyBillRepository pharmacyBillRepository;

    public PatientServiceImpl(
            UserRepository userRepository,
            AppointmentRepository appointmentRepository,
            ConsultationRepository consultationRepository,
            VitalRepository vitalRepository,
            PrescriptionRepository prescriptionRepository,
            LabTestRepository labTestRepository,
            AdmissionRepository admissionRepository,
            DischargeSummaryRepository dischargeSummaryRepository,
            HospitalBillRepository hospitalBillRepository,
            PharmacyBillRepository pharmacyBillRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.consultationRepository = consultationRepository;
        this.vitalRepository = vitalRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.labTestRepository = labTestRepository;
        this.admissionRepository = admissionRepository;
        this.dischargeSummaryRepository = dischargeSummaryRepository;
        this.hospitalBillRepository = hospitalBillRepository;
        this.pharmacyBillRepository = pharmacyBillRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerPatient(User patient) {

        if (userRepository.existsByEmail(patient.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Long patientId = userRepository.findByRole("PATIENT")
                .stream()
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(10000L);

        patient.setId(patientId + 1);

        patient.setRole("PATIENT");
        patient.setStatus("ACTIVE");

        patient.setPassword(
                passwordEncoder.encode(patient.getPassword())
        );

        patient.setUhid(
                "UHID" + System.currentTimeMillis()
        );

        return userRepository.save(patient);
    }
    @Override
    public User getPatientById(Long patientId) {

        return userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    @Override
    public User getPatientByUhid(String uhid) {

        return userRepository.findByUhid(uhid)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    @Override
    public Appointment bookAppointment(Appointment appointment) {

        appointment.setStatus("PENDING");

        String appointmentId = "APT" + System.currentTimeMillis();
        appointment.setAppointmentId(appointmentId);

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getPatientAppointments(Long patientId) {

        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Consultation> getPatientConsultations(Long patientId) {

        return consultationRepository.findByPatientId(patientId);
    }

    @Override
    public List<Vital> getPatientVitals(Long patientId) {

        return vitalRepository.findByPatientId(patientId);
    }

    @Override
    public List<Prescription> getPatientPrescriptions(Long patientId) {

        return prescriptionRepository.findByPatientId(patientId);
    }

    @Override
    public List<LabTest> getPatientLabTests(Long patientId) {

        return labTestRepository.findByPatientId(patientId);
    }

    @Override
    public List<Admission> getPatientAdmissions(Long patientId) {

        return admissionRepository.findByPatientId(patientId);
    }

    @Override
    public DischargeSummary getDischargeSummary(Long admissionId) {

        return dischargeSummaryRepository.findByAdmissionId(admissionId)
                .orElseThrow(() -> new RuntimeException("Discharge summary not found"));
    }

    @Override
    public List<HospitalBill> getHospitalBills(Long patientId) {

        return hospitalBillRepository.findByPatientId(patientId);
    }

    @Override
    public List<PharmacyBill> getPharmacyBills(Long patientId) {

        return pharmacyBillRepository.findByPatientId(patientId);
    }
}
package com.hospital.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hospital.entity.Appointment;
import com.hospital.entity.Consultation;
import com.hospital.entity.PharmacyBill;
import com.hospital.entity.Prescription;
import com.hospital.entity.User;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.ConsultationRepository;
import com.hospital.repository.PharmacyBillRepository;
import com.hospital.repository.PrescriptionRepository;
import com.hospital.repository.UserRepository;

@Service
public class PharmacistServiceImpl implements PharmacistService {

    private final PrescriptionRepository prescriptionRepository;
    private final PharmacyBillRepository pharmacyBillRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final UserRepository userRepository;

    public PharmacistServiceImpl(
            PrescriptionRepository prescriptionRepository,
            PharmacyBillRepository pharmacyBillRepository,
            AppointmentRepository appointmentRepository,
            ConsultationRepository consultationRepository,
            UserRepository userRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.pharmacyBillRepository = pharmacyBillRepository;
        this.appointmentRepository = appointmentRepository;
        this.consultationRepository = consultationRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET ALL PRESCRIPTIONS
    // =========================================================

    @Override
    public List<Prescription> getAllPrescriptions() {

        return prescriptionRepository.findAll();
    }


    // =========================================================
    // GET ALL PHARMACY BILLS
    // =========================================================

    @Override
    public List<PharmacyBill> getAllPharmacyBills() {

        return pharmacyBillRepository.findAll();
    }


    // =========================================================
    // GET PATIENT PRESCRIPTIONS
    // =========================================================

    @Override
    public List<Prescription> getPatientPrescriptions(Long patientId) {

        return prescriptionRepository.findByPatientId(patientId);
    }


    // =========================================================
    // GET PATIENT APPOINTMENTS
    // =========================================================

    @Override
    public List<Appointment> getPatientAppointments(Long patientId) {

        return appointmentRepository.findByPatientId(patientId);
    }


    // =========================================================
    // GET PRESCRIPTIONS FOR SPECIFIC APPOINTMENT
    // =========================================================

    @Override
    public List<Prescription> getPrescriptionsByAppointment(
            Long appointmentId) {

        Consultation consultation =
                consultationRepository
                        .findByAppointmentId(appointmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No consultation found for this appointment"
                                ));

        return prescriptionRepository
                .findByConsultationId(consultation.getId());
    }


    // =========================================================
    // CREATE PHARMACY BILL
    // =========================================================

    @Override
    public PharmacyBill createPharmacyBill(
            PharmacyBill pharmacyBill) {

        // -------------------------
        // Validate Patient
        // -------------------------

        if (pharmacyBill.getPatient() == null ||
                pharmacyBill.getPatient().getId() == null) {

            throw new RuntimeException(
                    "Patient ID is required");
        }


        // -------------------------
        // Validate Prescription
        // -------------------------

        if (pharmacyBill.getPrescription() == null ||
                pharmacyBill.getPrescription().getId() == null) {

            throw new RuntimeException(
                    "Prescription ID is required");
        }


        // -------------------------
        // Validate Price
        // -------------------------

        if (pharmacyBill.getPricePerTablet() == null ||
                pharmacyBill.getPricePerTablet() <= 0) {

            throw new RuntimeException(
                    "Price per tablet is required");
        }


        // -------------------------
        // Validate Quantity
        // -------------------------

        if (pharmacyBill.getQuantity() == null ||
                pharmacyBill.getQuantity() <= 0) {

            throw new RuntimeException(
                    "Quantity is required");
        }


        // -------------------------
        // Find Patient
        // -------------------------

        User patient = userRepository.findById(
                pharmacyBill.getPatient().getId()
        ).orElseThrow(() ->
                new RuntimeException(
                        "Patient not found")
        );


        // -------------------------
        // Find Prescription
        // -------------------------

        Prescription prescription =
                prescriptionRepository.findById(
                        pharmacyBill.getPrescription().getId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Prescription not found")
                );


        // -------------------------
        // Check Prescription
        // belongs to Patient
        // -------------------------

        if (prescription.getPatient() == null ||
                prescription.getPatient().getId() == null ||
                !prescription.getPatient()
                        .getId()
                        .equals(patient.getId())) {

            throw new RuntimeException(
                    "Prescription does not belong to this patient");
        }


        // -------------------------
        // Set Patient
        // -------------------------

        pharmacyBill.setPatient(patient);


        // -------------------------
        // Set Prescription
        // -------------------------

        pharmacyBill.setPrescription(prescription);


        // -------------------------
        // Get Medicine Name
        // directly from prescription
        // -------------------------

        pharmacyBill.setMedicineName(
                prescription.getMedicine()
        );


        // -------------------------
        // Calculate Total
        // -------------------------

        double totalAmount =
                pharmacyBill.getPricePerTablet()
                * pharmacyBill.getQuantity();

        pharmacyBill.setTotalAmount(totalAmount);


        // -------------------------
        // Generate Bill Number
        // -------------------------

        pharmacyBill.setBillNumber(
                "PHB" + System.currentTimeMillis()
        );


        // -------------------------
        // Initial Status
        // -------------------------

        pharmacyBill.setPaymentStatus("PENDING");

        pharmacyBill.setDispensingStatus("PENDING");


        // -------------------------
        // Bill Date
        // -------------------------

        pharmacyBill.setBillDate(
                LocalDateTime.now()
        );


        // -------------------------
        // Save Bill
        // -------------------------

        return pharmacyBillRepository.save(
                pharmacyBill
        );
    }


    // =========================================================
    // COMPLETE PAYMENT
    // =========================================================

    @Override
    public PharmacyBill completePayment(Long billId) {

        PharmacyBill bill =
                pharmacyBillRepository.findById(billId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pharmacy bill not found")
                        );

        bill.setPaymentStatus("PAID");

        return pharmacyBillRepository.save(bill);
    }


    // =========================================================
    // DISPENSE MEDICINE
    // =========================================================

    @Override
    public PharmacyBill dispenseMedicine(Long billId) {

        PharmacyBill bill =
                pharmacyBillRepository.findById(billId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Pharmacy bill not found")
                        );


        // -------------------------
        // Payment Check
        // -------------------------

        if (!"PAID".equals(bill.getPaymentStatus())) {

            throw new RuntimeException(
                    "Payment is not completed");
        }


        // -------------------------
        // No stock operation
        // -------------------------
        // Medicine is dispensed based
        // on the prescription and bill.
        // No inventory is maintained.


        bill.setDispensingStatus("DISPENSED");

        return pharmacyBillRepository.save(bill);
    }


    // =========================================================
    // GET PATIENT PHARMACY BILLS
    // =========================================================

    @Override
    public List<PharmacyBill> getPatientPharmacyBills(
            Long patientId) {

        return pharmacyBillRepository
                .findByPatientId(patientId);
    }
}
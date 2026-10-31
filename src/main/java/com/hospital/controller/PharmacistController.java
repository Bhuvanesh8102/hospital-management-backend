package com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.entity.Appointment;
import com.hospital.entity.PharmacyBill;
import com.hospital.entity.Prescription;
import com.hospital.service.PharmacistService;

@RestController
@RequestMapping("/pharmacist")
public class PharmacistController {

    private final PharmacistService pharmacistService;

    public PharmacistController(PharmacistService pharmacistService) {
        this.pharmacistService = pharmacistService;
    }


    // =========================================================
    // PATIENT PRESCRIPTIONS
    // =========================================================

    @GetMapping("/patient/{patientId}/prescriptions")
    public ResponseEntity<List<Prescription>> getPatientPrescriptions(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                pharmacistService.getPatientPrescriptions(patientId)
        );
    }


    // =========================================================
    // PATIENT APPOINTMENTS
    // =========================================================

    @GetMapping("/patient/{patientId}/appointments")
    public ResponseEntity<List<Appointment>> getPatientAppointments(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                pharmacistService.getPatientAppointments(patientId)
        );
    }


    // =========================================================
    // PRESCRIPTIONS FOR SELECTED APPOINTMENT
    // =========================================================

    @GetMapping("/appointment/{appointmentId}/prescriptions")
    public ResponseEntity<?> getPrescriptionsByAppointment(
            @PathVariable Long appointmentId) {

        try {

            return ResponseEntity.ok(
                    pharmacistService
                            .getPrescriptionsByAppointment(appointmentId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // CREATE PHARMACY BILL
    // =========================================================

    @PostMapping("/bill")
    public ResponseEntity<?> createPharmacyBill(
            @RequestBody PharmacyBill pharmacyBill) {

        try {

            PharmacyBill savedBill =
                    pharmacistService
                            .createPharmacyBill(pharmacyBill);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedBill);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // COMPLETE PAYMENT
    // =========================================================

    @PutMapping("/bill/{billId}/payment")
    public ResponseEntity<?> completePayment(
            @PathVariable Long billId) {

        try {

            return ResponseEntity.ok(
                    pharmacistService.completePayment(billId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // DISPENSE MEDICINE
    // =========================================================

    @PutMapping("/bill/{billId}/dispense")
    public ResponseEntity<?> dispenseMedicine(
            @PathVariable Long billId) {

        try {

            return ResponseEntity.ok(
                    pharmacistService.dispenseMedicine(billId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // PATIENT PHARMACY BILLS
    // =========================================================

    @GetMapping("/patient/{patientId}/bills")
    public ResponseEntity<List<PharmacyBill>> getPatientPharmacyBills(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                pharmacistService
                        .getPatientPharmacyBills(patientId)
        );
    }


    // =========================================================
    // ALL PRESCRIPTIONS
    // =========================================================

    @GetMapping("/prescriptions")
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {

        return ResponseEntity.ok(
                pharmacistService.getAllPrescriptions()
        );
    }


    // =========================================================
    // ALL PHARMACY BILLS
    // =========================================================

    @GetMapping("/bills")
    public ResponseEntity<List<PharmacyBill>> getAllPharmacyBills() {

        return ResponseEntity.ok(
                pharmacistService.getAllPharmacyBills()
        );
    }
}
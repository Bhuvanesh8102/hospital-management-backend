package com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.entity.Admission;
import com.hospital.entity.Appointment;
import com.hospital.entity.Consultation;
import com.hospital.entity.HospitalBill;
import com.hospital.entity.LabTest;
import com.hospital.entity.PharmacyBill;
import com.hospital.entity.Prescription;
import com.hospital.entity.User;
import com.hospital.entity.Vital;
import com.hospital.service.PatientService;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // =========================================================
    // PATIENT REGISTRATION
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(
            @RequestBody User patient) {

        try {

            User registeredPatient =
                    patientService.registerPatient(patient);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(registeredPatient);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // GET PATIENT BY ID
    // =========================================================

    @GetMapping("/{patientId}")
    public ResponseEntity<?> getPatientById(
            @PathVariable Long patientId) {

        try {

            return ResponseEntity.ok(
                    patientService.getPatientById(patientId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // GET PATIENT BY UHID
    // =========================================================

    @GetMapping("/uhid/{uhid}")
    public ResponseEntity<?> getPatientByUhid(
            @PathVariable String uhid) {

        try {

            return ResponseEntity.ok(
                    patientService.getPatientByUhid(uhid)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // BOOK APPOINTMENT
    // =========================================================

    @PostMapping("/appointment")
    public ResponseEntity<?> bookAppointment(
            @RequestBody Appointment appointment) {

        try {

            Appointment savedAppointment =
                    patientService.bookAppointment(appointment);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedAppointment);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // VIEW APPOINTMENTS
    // GET /patient/10001/appointments
    // =========================================================

    @GetMapping("/{patientId}/appointments")
    public ResponseEntity<List<Appointment>>
    getPatientAppointments(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getPatientAppointments(patientId)
        );
    }


    // =========================================================
    // VIEW CONSULTATIONS
    // =========================================================

    @GetMapping("/{patientId}/consultations")
    public ResponseEntity<List<Consultation>>
    getPatientConsultations(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getPatientConsultations(patientId)
        );
    }


    // =========================================================
    // VIEW VITALS
    // =========================================================

    @GetMapping("/{patientId}/vitals")
    public ResponseEntity<List<Vital>>
    getPatientVitals(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getPatientVitals(patientId)
        );
    }


    // =========================================================
    // VIEW PRESCRIPTIONS
    // GET /patient/10001/prescriptions
    // =========================================================

    @GetMapping("/{patientId}/prescriptions")
    public ResponseEntity<List<Prescription>>
    getPatientPrescriptions(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getPatientPrescriptions(patientId)
        );
    }


    // =========================================================
    // VIEW LAB TESTS
    // Existing endpoint
    // =========================================================

    @GetMapping("/{patientId}/lab-tests")
    public ResponseEntity<List<LabTest>>
    getPatientLabTests(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getPatientLabTests(patientId)
        );
    }


    // =========================================================
    // VIEW LAB REPORTS
    // Frontend uses:
    // GET /patient/{patientId}/lab-reports
    //
    // We use the existing service method because your
    // backend already stores lab tests/reports through LabTest.
    // =========================================================

    @GetMapping("/{patientId}/lab-reports")
    public ResponseEntity<List<LabTest>>
    getPatientLabReports(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getPatientLabTests(patientId)
        );
    }


    // =========================================================
    // VIEW ADMISSIONS
    // =========================================================

    @GetMapping("/{patientId}/admissions")
    public ResponseEntity<List<Admission>>
    getPatientAdmissions(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getPatientAdmissions(patientId)
        );
    }


    // =========================================================
    // VIEW DISCHARGE SUMMARY
    // =========================================================

    @GetMapping("/admission/{admissionId}/discharge-summary")
    public ResponseEntity<?> getDischargeSummary(
            @PathVariable Long admissionId) {

        try {

            return ResponseEntity.ok(
                    patientService.getDischargeSummary(admissionId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // VIEW HOSPITAL BILLS
    // Existing endpoint
    // =========================================================

    @GetMapping("/{patientId}/hospital-bills")
    public ResponseEntity<List<HospitalBill>>
    getHospitalBills(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getHospitalBills(patientId)
        );
    }


    // =========================================================
    // VIEW BILLS
    // Frontend uses:
    // GET /patient/{patientId}/bills
    //
    // Currently this returns hospital bills.
    // Pharmacy bills remain available separately below.
    // =========================================================

    @GetMapping("/{patientId}/bills")
    public ResponseEntity<List<HospitalBill>>
    getBills(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getHospitalBills(patientId)
        );
    }


    // =========================================================
    // VIEW PHARMACY BILLS
    // =========================================================

    @GetMapping("/{patientId}/pharmacy-bills")
    public ResponseEntity<List<PharmacyBill>>
    getPharmacyBills(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                patientService.getPharmacyBills(patientId)
        );
    }
    
    
}
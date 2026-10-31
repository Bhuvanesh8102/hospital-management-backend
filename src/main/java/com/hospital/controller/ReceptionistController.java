package com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.entity.Admission;
import com.hospital.entity.Appointment;
import com.hospital.entity.HospitalBill;
import com.hospital.entity.User;
import com.hospital.service.ReceptionistService;

@RestController
@RequestMapping("/receptionist")
public class ReceptionistController {

    private final ReceptionistService receptionistService;

    public ReceptionistController(
            ReceptionistService receptionistService) {

        this.receptionistService = receptionistService;
    }

    // =========================================================
    // APPOINTMENTS
    // =========================================================

    // View all appointments
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {

        return ResponseEntity.ok(
                receptionistService.getAllAppointments()
        );
    }

    // View appointments for a particular receptionist
    //
    // Example:
    // GET /receptionist/50001/appointments
    //
    @GetMapping("/{userId}/appointments")
    public ResponseEntity<List<Appointment>> getReceptionistAppointments(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                receptionistService.getAllAppointments()
        );
    }

    // Search appointment using Appointment ID
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getAppointmentByAppointmentId(
            @PathVariable String appointmentId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .getAppointmentByAppointmentId(
                                    appointmentId
                            )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Confirm appointment
    @PutMapping("/appointment/{appointmentId}/confirm")
    public ResponseEntity<?> confirmAppointment(
            @PathVariable Long appointmentId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .confirmAppointment(appointmentId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Cancel appointment
    @PutMapping("/appointment/{appointmentId}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long appointmentId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .cancelAppointment(appointmentId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Check in patient
    @PutMapping("/appointment/{appointmentId}/check-in")
    public ResponseEntity<?> checkInAppointment(
            @PathVariable Long appointmentId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .checkInAppointment(appointmentId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Mark appointment as no-show
    @PutMapping("/appointment/{appointmentId}/no-show")
    public ResponseEntity<?> markNoShow(
            @PathVariable Long appointmentId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .markNoShow(appointmentId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Create appointment for walk-in/existing patient
    @PostMapping("/appointment")
    public ResponseEntity<?> createAppointment(
            @RequestBody Appointment appointment) {

        try {

            Appointment savedAppointment =
                    receptionistService
                            .createAppointment(appointment);

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
    // PATIENT SEARCH
    // =========================================================

    // Search patient by UHID
    @GetMapping("/patient/uhid/{uhid}")
    public ResponseEntity<?> searchPatientByUhid(
            @PathVariable String uhid) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .searchPatientByUhid(uhid)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Search patient by mobile
    @GetMapping("/patient/mobile/{mobile}")
    public ResponseEntity<?> searchPatientByMobile(
            @PathVariable String mobile) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .searchPatientByMobile(mobile)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Search patient by name
    @GetMapping("/patient/name/{name}")
    public ResponseEntity<?> searchPatientByName(
            @PathVariable String name) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .searchPatientByName(name)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // ADMISSION
    // =========================================================

    // Create admission
    @PostMapping("/admission")
    public ResponseEntity<?> createAdmission(
            @RequestBody Admission admission) {

        try {

            Admission savedAdmission =
                    receptionistService
                            .createAdmission(admission);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedAdmission);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Process discharge
    @PutMapping("/admission/{admissionId}/discharge")
    public ResponseEntity<?> processDischarge(
            @PathVariable Long admissionId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .processDischarge(admissionId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // BILLING
    // =========================================================

    // Create hospital bill
    @PostMapping("/billing")
    public ResponseEntity<?> createHospitalBill(
            @RequestBody HospitalBill hospitalBill) {

        try {

            HospitalBill savedBill =
                    receptionistService
                            .createHospitalBill(hospitalBill);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedBill);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // View hospital bill
    @GetMapping("/billing/{billId}")
    public ResponseEntity<?> getHospitalBill(
            @PathVariable Long billId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .getHospitalBill(billId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Complete hospital payment
    @PutMapping("/billing/{billId}/payment")
    public ResponseEntity<?> completeHospitalPayment(
            @PathVariable Long billId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .completeHospitalPayment(billId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Complete final billing
    @PutMapping("/billing/{billId}/final-payment")
    public ResponseEntity<?> completeFinalBilling(
            @PathVariable Long billId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .completeFinalBilling(billId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
    
    @PostMapping("/patient")
    public ResponseEntity<?> registerWalkInPatient(
            @RequestBody User patient) {

        try {
            User savedPatient =
                    receptionistService.registerWalkInPatient(patient);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedPatient);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
 // View all admissions for a patient
    @GetMapping("/patient/{patientId}/admissions")
    public ResponseEntity<?> getPatientAdmissions(
            @PathVariable Long patientId) {

        try {

            return ResponseEntity.ok(
                    receptionistService
                            .getPatientAdmissions(patientId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
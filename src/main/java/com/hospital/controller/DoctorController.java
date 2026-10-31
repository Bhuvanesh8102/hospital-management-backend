package com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.entity.Appointment;
import com.hospital.entity.Consultation;
import com.hospital.entity.LabTest;
import com.hospital.entity.Prescription;
import com.hospital.entity.User;
import com.hospital.entity.Vital;
import com.hospital.repository.AppointmentRepository;
import com.hospital.service.DoctorService;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final AppointmentRepository appointmentRepository;

    private final DoctorService doctorService;


    public DoctorController(
            DoctorService doctorService, AppointmentRepository appointmentRepository) {

        this.doctorService = doctorService;

        this.appointmentRepository = appointmentRepository;
    }


    // =========================================================
    // APPOINTMENTS
    // =========================================================

    @GetMapping("/{doctorId}/appointments")
    public ResponseEntity<List<Appointment>>
    getDoctorAppointments(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                doctorService.getDoctorAppointments(
                        doctorId
                )
        );
    }


    @GetMapping(
            "/{doctorId}/appointments/status/{status}"
    )
    public ResponseEntity<List<Appointment>>
    getDoctorAppointmentsByStatus(
            @PathVariable Long doctorId,
            @PathVariable String status) {

        return ResponseEntity.ok(
                doctorService
                        .getDoctorAppointmentsByStatus(
                                doctorId,
                                status
                        )
        );
    }


    // =========================================================
    // PATIENT DETAILS
    // =========================================================

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientById(
            @PathVariable Long patientId) {

        try {
            return ResponseEntity.ok(
                    doctorService.getPatientById(patientId)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // PATIENT VITALS
    // =========================================================

    @GetMapping("/patient/{patientId}/vitals")
    public ResponseEntity<List<Vital>>
    getPatientVitals(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                doctorService.getPatientVitals(
                        patientId
                )
        );
    }


    // =========================================================
    // CONSULTATION
    // =========================================================

    @PostMapping("/consultation")
    public ResponseEntity<?> createConsultation(
            @RequestBody Consultation consultation) {

        try {

            Consultation savedConsultation =
                    doctorService.createConsultation(
                            consultation
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedConsultation);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    @GetMapping(
            "/consultation/appointment/{appointmentId}"
    )
    public ResponseEntity<?> getConsultationByAppointment(
            @PathVariable Long appointmentId) {

        try {

            return ResponseEntity.ok(
                    doctorService
                            .getConsultationByAppointment(
                                    appointmentId
                            )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    @GetMapping("/{doctorId}/consultations")
    public ResponseEntity<List<Consultation>>
    getDoctorConsultations(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                doctorService.getDoctorConsultations(
                        doctorId
                )
        );
    }


    // =========================================================
    // PRESCRIPTIONS
    // =========================================================

    @PostMapping("/prescription")
    public ResponseEntity<?> createPrescription(
            @RequestBody Prescription prescription) {

        try {

            Prescription savedPrescription =
                    doctorService.createPrescription(
                            prescription
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedPrescription);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    @GetMapping(
            "/patient/{patientId}/prescriptions"
    )
    public ResponseEntity<List<Prescription>>
    getPatientPrescriptions(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                doctorService.getPatientPrescriptions(
                        patientId
                )
        );
    }


    // =========================================================
    // LAB
    // =========================================================

    @PostMapping("/lab-test")
    public ResponseEntity<?> orderLabTest(
            @RequestBody LabTest labTest) {

        try {

            LabTest savedLabTest =
                    doctorService.orderLabTest(
                            labTest
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedLabTest);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    @GetMapping(
            "/patient/{patientId}/lab-tests"
    )
    public ResponseEntity<List<LabTest>>
    getPatientLabTests(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                doctorService.getPatientLabTests(
                        patientId
                )
        );
    }


    // =========================================================
    // RECOMMENDATIONS
    // =========================================================

    @PutMapping(
            "/consultation/{consultationId}/recommend-admission"
    )
    public ResponseEntity<?> recommendAdmission(
            @PathVariable Long consultationId) {

        try {

            return ResponseEntity.ok(
                    doctorService
                            .recommendAdmission(
                                    consultationId
                            )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    @PutMapping(
            "/consultation/{consultationId}/recommend-operation"
    )
    public ResponseEntity<?> recommendOperation(
            @PathVariable Long consultationId) {

        try {

            return ResponseEntity.ok(
                    doctorService
                            .recommendOperation(
                                    consultationId
                            )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    @PutMapping(
            "/consultation/{consultationId}/recommend-discharge"
    )
    public ResponseEntity<?> recommendDischarge(
            @PathVariable Long consultationId) {

        try {

            return ResponseEntity.ok(
                    doctorService
                            .recommendDischarge(
                                    consultationId
                            )
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }


    // =========================================================
    // SPECIALIZATIONS
    // =========================================================

    @GetMapping("/specializations")
    public ResponseEntity<List<String>>
    getSpecializations() {

        return ResponseEntity.ok(
                doctorService.getSpecializations()
        );
    }


    @GetMapping(
            "/specialization/{specialization}"
    )
    public ResponseEntity<List<User>>
    getDoctorsBySpecialization(
            @PathVariable String specialization) {

        return ResponseEntity.ok(
                doctorService
                        .getDoctorsBySpecialization(
                                specialization
                        )
        );
    }
    @GetMapping("/{doctorId}")
    public ResponseEntity<?> getDoctorById(
            @PathVariable Long doctorId) {

        try {

            return ResponseEntity.ok(
                    doctorService.getDoctorById(doctorId)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
    
    @PutMapping("/appointment/{appointmentId}/verify")
    public ResponseEntity<?> verifyAppointment(
            @PathVariable Long appointmentId) {

        try {

            Appointment appointment =
                    doctorService.verifyAppointment(
                            appointmentId
                    );

            return ResponseEntity.ok(appointment);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
    
    @GetMapping("/appointment/{appointmentId}/prescriptions")
    public ResponseEntity<?> getPrescriptionsByAppointment(
            @PathVariable Long appointmentId) {

        try {
            return ResponseEntity.ok(
                    doctorService.getPrescriptionsByAppointment(
                            appointmentId
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
    
    @GetMapping("/appointment/{appointmentId}/vitals")
    public ResponseEntity<List<Vital>> getVitalsByAppointment(
            @PathVariable Long appointmentId) {

        return ResponseEntity.ok(
                doctorService.getVitalsByAppointment(appointmentId)
        );
    }
}
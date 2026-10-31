package com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.entity.Appointment;
import com.hospital.entity.User;
import com.hospital.entity.Vital;
import com.hospital.service.NurseService;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    private final NurseService nurseService;

    public NurseController(NurseService nurseService) {
        this.nurseService = nurseService;
    }

    // =========================================================
    // VIEW PATIENTS
    // GET /nurse/{nurseId}/patients
    // =========================================================
    @GetMapping("/{nurseId}/patients")
    public ResponseEntity<?> getPatients(
            @PathVariable Long nurseId) {

        try {
            return ResponseEntity.ok(
                    nurseService.getPatients(nurseId)
            );

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // RECORD PATIENT VITALS
    // POST /nurse/vitals
    // =========================================================
    @PostMapping("/vitals")
    public ResponseEntity<?> recordVitals(
            @RequestBody Vital vital) {

        try {

            Vital savedVital =
                    nurseService.recordVitals(vital);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedVital);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // =========================================================
    // VIEW VITALS FOR AN APPOINTMENT
    // GET /nurse/appointment/{appointmentId}/vitals
    // =========================================================
    @GetMapping("/appointment/{appointmentId}/vitals")
    public ResponseEntity<List<Vital>> getVitalsByAppointment(
            @PathVariable Long appointmentId) {

        return ResponseEntity.ok(
                nurseService.getVitalsByAppointment(appointmentId)
        );
    }

    // =========================================================
    // VIEW PATIENT'S PREVIOUS VITALS
    // GET /nurse/patient/{patientId}/vitals
    // =========================================================
    @GetMapping("/patient/{patientId}/vitals")
    public ResponseEntity<List<Vital>> getPatientVitals(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                nurseService.getPatientVitals(patientId)
        );
    }
    
    @GetMapping("/patient/{patientId}/appointments")
    public ResponseEntity<List<Appointment>> getPatientAppointments(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                nurseService.getPatientAppointments(patientId)
        );
    }
}
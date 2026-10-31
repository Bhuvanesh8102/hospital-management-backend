package com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.entity.Admission;
import com.hospital.entity.Bed;
import com.hospital.service.BedInchargeService;

@RestController
@RequestMapping("/bed")
public class BedInchargeController {

    private final BedInchargeService bedInchargeService;

    public BedInchargeController(BedInchargeService bedInchargeService) {
        this.bedInchargeService = bedInchargeService;
    }

    // View all beds
    @GetMapping("/beds")
    public ResponseEntity<List<Bed>> getAllBeds() {
        return ResponseEntity.ok(
                bedInchargeService.getAllBeds());
    }

    // View available beds
    @GetMapping("/beds/available")
    public ResponseEntity<List<Bed>> getAvailableBeds() {
        return ResponseEntity.ok(
                bedInchargeService.getAvailableBeds());
    }

    // View beds by ward
    @GetMapping("/beds/ward/{wardName}")
    public ResponseEntity<List<Bed>> getBedsByWard(
            @PathVariable String wardName) {

        return ResponseEntity.ok(
                bedInchargeService.getBedsByWard(wardName));
    }

    // Add new bed
    @PostMapping("/bed")
    public ResponseEntity<?> addBed(
            @RequestBody Bed bed) {

        try {
            Bed savedBed = bedInchargeService.addBed(bed);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedBed);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Update bed
    @PutMapping("/bed/{bedId}")
    public ResponseEntity<?> updateBed(
            @PathVariable Long bedId,
            @RequestBody Bed bed) {

        try {
            return ResponseEntity.ok(
                    bedInchargeService.updateBed(
                            bedId, bed));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Assign bed to admission
    @PutMapping("/admission/{admissionId}/assign/{bedId}")
    public ResponseEntity<?> assignBed(
            @PathVariable Long admissionId,
            @PathVariable Long bedId) {

        try {
            return ResponseEntity.ok(
                    bedInchargeService.assignBed(
                            admissionId, bedId));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Release bed after discharge
    @PutMapping("/bed/{bedId}/release")
    public ResponseEntity<?> releaseBed(
            @PathVariable Long bedId) {

        try {
            return ResponseEntity.ok(
                    bedInchargeService.releaseBed(bedId));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Transfer patient to another bed
    @PutMapping("/admission/{admissionId}/transfer/{newBedId}")
    public ResponseEntity<?> transferBed(
            @PathVariable Long admissionId,
            @PathVariable Long newBedId) {

        try {
            return ResponseEntity.ok(
                    bedInchargeService.transferBed(
                            admissionId, newBedId));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // View admission details
    @GetMapping("/admission/{admissionId}")
    public ResponseEntity<?> getAdmission(
            @PathVariable Long admissionId) {

        try {
            return ResponseEntity.ok(
                    bedInchargeService.getAdmission(admissionId));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
    
    @GetMapping("/admissions")
    public ResponseEntity<List<Admission>> getAllAdmissions() {
        return ResponseEntity.ok(
                bedInchargeService.getAllAdmissions()
        );
    }
}
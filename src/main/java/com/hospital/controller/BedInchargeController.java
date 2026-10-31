package com.hospital.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.entity.Admission;
import com.hospital.entity.Bed;
import com.hospital.entity.Room;
import com.hospital.service.BedInchargeService;

@RestController
@RequestMapping("/bed")
public class BedInchargeController {

    private final BedInchargeService bedInchargeService;

    public BedInchargeController(BedInchargeService bedInchargeService) {
        this.bedInchargeService = bedInchargeService;
    }

    // ================= BED MANAGEMENT =================

    @GetMapping("/beds")
    public ResponseEntity<List<Bed>> getAllBeds() {
        return ResponseEntity.ok(bedInchargeService.getAllBeds());
    }

    @GetMapping("/beds/available")
    public ResponseEntity<List<Bed>> getAvailableBeds() {
        return ResponseEntity.ok(bedInchargeService.getAvailableBeds());
    }

    @GetMapping("/beds/ward/{wardName}")
    public ResponseEntity<List<Bed>> getBedsByWard(
            @PathVariable String wardName) {

        return ResponseEntity.ok(
                bedInchargeService.getBedsByWard(wardName));
    }

    @PostMapping("/bed")
    public ResponseEntity<Bed> addBed(@RequestBody Bed bed) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bedInchargeService.addBed(bed));
    }

    @PutMapping("/bed/{bedId}")
    public ResponseEntity<Bed> updateBed(
            @PathVariable Long bedId,
            @RequestBody Bed bed) {

        return ResponseEntity.ok(
                bedInchargeService.updateBed(bedId, bed));
    }

    @PutMapping("/admission/{admissionId}/assign/{bedId}")
    public ResponseEntity<Bed> assignBed(
            @PathVariable Long admissionId,
            @PathVariable Long bedId) {

        return ResponseEntity.ok(
                bedInchargeService.assignBed(admissionId, bedId));
    }

    @PutMapping("/bed/{bedId}/release")
    public ResponseEntity<Bed> releaseBed(@PathVariable Long bedId) {
        return ResponseEntity.ok(
                bedInchargeService.releaseBed(bedId));
    }

    @PutMapping("/admission/{admissionId}/transfer/{newBedId}")
    public ResponseEntity<Admission> transferBed(
            @PathVariable Long admissionId,
            @PathVariable Long newBedId) {

        return ResponseEntity.ok(
                bedInchargeService.transferBed(admissionId, newBedId));
    }

    // ================= ROOM MANAGEMENT =================

    @GetMapping("/rooms")
    public ResponseEntity<List<Map<String, Object>>> getAllRooms() {
        return ResponseEntity.ok(
                bedInchargeService.getAllRooms());
    }

    @GetMapping("/rooms/ward/{wardName}")
    public ResponseEntity<List<Map<String, Object>>> getRoomsByWard(
            @PathVariable String wardName) {

        return ResponseEntity.ok(
                bedInchargeService.getRoomsByWard(wardName));
    }

    @PostMapping("/room")
    public ResponseEntity<Room> addRoom(@RequestBody Room room) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bedInchargeService.addRoom(room));
    }

    @PutMapping("/room/{roomId}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable Long roomId,
            @RequestBody Room room) {

        return ResponseEntity.ok(
                bedInchargeService.updateRoom(roomId, room));
    }

    // ================= WARD MANAGEMENT =================

    @GetMapping("/wards")
    public ResponseEntity<List<String>> getAllWardNames() {
        return ResponseEntity.ok(
                bedInchargeService.getAllWardNames());
    }

    @PostMapping("/ward")
    public ResponseEntity<String> addWard(
            @RequestBody Map<String, String> request) {

        bedInchargeService.addWard(request.get("wardName"));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Ward added successfully");
    }

    // ================= ADMISSION MANAGEMENT =================

    @GetMapping("/admission/{admissionId}")
    public ResponseEntity<Admission> getAdmission(
            @PathVariable Long admissionId) {

        return ResponseEntity.ok(
                bedInchargeService.getAdmission(admissionId));
    }

    @GetMapping("/admissions")
    public ResponseEntity<List<Admission>> getAllAdmissions() {
        return ResponseEntity.ok(
                bedInchargeService.getAllAdmissions());
    }

    // ================= BED AVAILABILITY DASHBOARD =================

    @GetMapping("/beds/summary")
    public ResponseEntity<Map<String, Object>> getBedAvailabilitySummary() {
        return ResponseEntity.ok(
                bedInchargeService.getBedAvailabilitySummary());
    }

    @GetMapping("/beds/ward-summary")
    public ResponseEntity<List<Map<String, Object>>> getWardWiseBedSummary() {
        return ResponseEntity.ok(
                bedInchargeService.getWardWiseBedSummary());
    }
}

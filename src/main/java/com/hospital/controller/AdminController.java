package com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.entity.Appointment;
import com.hospital.entity.HospitalBill;
import com.hospital.entity.User;
import com.hospital.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // View all staff
    @GetMapping("/staff")
    public ResponseEntity<List<User>> getAllStaff() {
        return ResponseEntity.ok(adminService.getAllStaff());
    }

    // View pending staff registrations
    @GetMapping("/staff/pending")
    public ResponseEntity<List<User>> getPendingStaff() {
        return ResponseEntity.ok(adminService.getPendingStaff());
    }

    // Approve staff
    @PutMapping("/staff/{userId}/approve")
    public ResponseEntity<?> approveStaff(@PathVariable Long userId) {

        try {
            return ResponseEntity.ok(adminService.approveStaff(userId));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Reject staff
    @PutMapping("/staff/{userId}/reject")
    public ResponseEntity<?> rejectStaff(@PathVariable Long userId) {

        try {
            return ResponseEntity.ok(adminService.rejectStaff(userId));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Activate staff
    @PutMapping("/staff/{userId}/activate")
    public ResponseEntity<?> activateStaff(@PathVariable Long userId) {

        try {
            return ResponseEntity.ok(adminService.activateStaff(userId));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Deactivate staff
    @PutMapping("/staff/{userId}/deactivate")
    public ResponseEntity<?> deactivateStaff(@PathVariable Long userId) {

        try {
            return ResponseEntity.ok(adminService.deactivateStaff(userId));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // View all patients
    @GetMapping("/patients")
    public ResponseEntity<List<User>> getAllPatients() {
        return ResponseEntity.ok(adminService.getAllPatients());
    }

    // View all doctors
    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    // View all nurses
    @GetMapping("/nurses")
    public ResponseEntity<List<User>> getAllNurses() {
        return ResponseEntity.ok(adminService.getAllNurses());
    }

    // View all receptionists
    @GetMapping("/receptionists")
    public ResponseEntity<List<User>> getAllReceptionists() {
        return ResponseEntity.ok(adminService.getAllReceptionists());
    }

    // View all pharmacists
    @GetMapping("/pharmacists")
    public ResponseEntity<List<User>> getAllPharmacists() {
        return ResponseEntity.ok(adminService.getAllPharmacists());
    }

    // View all lab in-charges
    @GetMapping("/lab-incharges")
    public ResponseEntity<List<User>> getAllLabIncharges() {
        return ResponseEntity.ok(adminService.getAllLabIncharges());
    }

    // View all bed in-charges
    @GetMapping("/bed-incharges")
    public ResponseEntity<List<User>> getAllBedIncharges() {
        return ResponseEntity.ok(adminService.getAllBedIncharges());
    }
    
    @DeleteMapping("/staff/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {

        try {
            adminService.deleteStaff(id);

            return ResponseEntity.ok(
                    "Staff deleted successfully");

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
 // View all appointments
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {

        return ResponseEntity.ok(
                adminService.getAllAppointments()
        );
    }
    
 // View all hospital billing
    @GetMapping("/billing")
    public ResponseEntity<List<HospitalBill>> getAllBilling() {

        return ResponseEntity.ok(
                adminService.getAllBilling()
        );
    }
}
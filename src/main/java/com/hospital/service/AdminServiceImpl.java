package com.hospital.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hospital.entity.Appointment;
import com.hospital.entity.HospitalBill;
import com.hospital.entity.User;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.HospitalBillRepository;
import com.hospital.repository.UserRepository;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalBillRepository hospitalBillRepository;

    public AdminServiceImpl(
            UserRepository userRepository,
            AppointmentRepository appointmentRepository,
            HospitalBillRepository hospitalBillRepository) {

        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.hospitalBillRepository = hospitalBillRepository;
    }

    // =========================================
    // VIEW ALL STAFF
    // =========================================

    @Override
    public List<User> getAllStaff() {

        List<User> staff = new ArrayList<>();

        staff.addAll(userRepository.findByRole("DOCTOR"));
        staff.addAll(userRepository.findByRole("NURSE"));
        staff.addAll(userRepository.findByRole("RECEPTIONIST"));
        staff.addAll(userRepository.findByRole("PHARMACIST"));
        staff.addAll(userRepository.findByRole("LAB_INCHARGE"));
        staff.addAll(userRepository.findByRole("BED_INCHARGE"));

        return staff;
    }

    // =========================================
    // VIEW PENDING STAFF
    // =========================================

    @Override
    public List<User> getPendingStaff() {
        return userRepository.findByStatus("PENDING");
    }

    // =========================================
    // APPROVE STAFF
    // =========================================

    @Override
    public User approveStaff(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found"));

        user.setStatus("APPROVED");

        return userRepository.save(user);
    }

    // =========================================
    // REJECT STAFF
    // =========================================

    @Override
    public User rejectStaff(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found"));

        user.setStatus("REJECTED");

        return userRepository.save(user);
    }

    // =========================================
    // ACTIVATE STAFF
    // =========================================

    @Override
    public User activateStaff(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found"));

        user.setStatus("ACTIVE");

        return userRepository.save(user);
    }

    // =========================================
    // DEACTIVATE STAFF
    // =========================================

    @Override
    public User deactivateStaff(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found"));

        user.setStatus("INACTIVE");

        return userRepository.save(user);
    }

    // =========================================
    // VIEW ALL PATIENTS
    // =========================================

    @Override
    public List<User> getAllPatients() {
        return userRepository.findByRole("PATIENT");
    }

    // =========================================
    // VIEW ALL DOCTORS
    // =========================================

    @Override
    public List<User> getAllDoctors() {
        return userRepository.findByRole("DOCTOR");
    }

    // =========================================
    // VIEW ALL NURSES
    // =========================================

    @Override
    public List<User> getAllNurses() {
        return userRepository.findByRole("NURSE");
    }

    // =========================================
    // VIEW ALL RECEPTIONISTS
    // =========================================

    @Override
    public List<User> getAllReceptionists() {
        return userRepository.findByRole("RECEPTIONIST");
    }

    // =========================================
    // VIEW ALL PHARMACISTS
    // =========================================

    @Override
    public List<User> getAllPharmacists() {
        return userRepository.findByRole("PHARMACIST");
    }

    // =========================================
    // VIEW ALL LAB IN-CHARGES
    // =========================================

    @Override
    public List<User> getAllLabIncharges() {
        return userRepository.findByRole("LAB_INCHARGE");
    }

    // =========================================
    // VIEW ALL BED IN-CHARGES
    // =========================================

    @Override
    public List<User> getAllBedIncharges() {
        return userRepository.findByRole("BED_INCHARGE");
    }

    // =========================================
    // DELETE STAFF
    // =========================================

    @Override
    public void deleteStaff(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Staff not found"));

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Admin cannot be deleted");
        }

        userRepository.delete(user);
    }

    // =========================================
    // VIEW ALL APPOINTMENTS
    // =========================================

    @Override
    public List<Appointment> getAllAppointments() {

        return appointmentRepository.findAll();
    }

    // =========================================
    // VIEW ALL BILLING
    // =========================================

    @Override
    public List<HospitalBill> getAllBilling() {

        return hospitalBillRepository.findAll();
    }
}
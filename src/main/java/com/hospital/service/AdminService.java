package com.hospital.service;

import java.util.List;

import com.hospital.entity.Appointment;
import com.hospital.entity.HospitalBill;
import com.hospital.entity.User;

public interface AdminService {

    List<User> getAllStaff();

    List<User> getPendingStaff();

    User approveStaff(Long userId);

    User rejectStaff(Long userId);

    User activateStaff(Long userId);

    User deactivateStaff(Long userId);

    List<User> getAllPatients();

    List<User> getAllDoctors();

    List<User> getAllNurses();

    List<User> getAllReceptionists();

    List<User> getAllPharmacists();

    List<User> getAllLabIncharges();

    List<User> getAllBedIncharges();

    void deleteStaff(Long id);

    List<Appointment> getAllAppointments();

    // Billing
    List<HospitalBill> getAllBilling();
}
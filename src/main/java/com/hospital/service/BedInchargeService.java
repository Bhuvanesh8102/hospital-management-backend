package com.hospital.service;

import java.util.List;
import java.util.Map;

import com.hospital.entity.Admission;
import com.hospital.entity.Bed;
import com.hospital.entity.Room;

public interface BedInchargeService {

    // Bed management
    List<Bed> getAllBeds();

    List<Bed> getAvailableBeds();

    List<Bed> getBedsByWard(String wardName);

    Bed addBed(Bed bed);

    Bed updateBed(Long bedId, Bed bed);

    Bed assignBed(Long admissionId, Long bedId);

    Bed releaseBed(Long bedId);

    Admission transferBed(Long admissionId, Long newBedId);

    // Room management
    List<Map<String, Object>> getAllRooms();

    List<Map<String, Object>> getRoomsByWard(String wardName);

    Room addRoom(Room room);

    Room updateRoom(Long roomId, Room room);

    // Admission management
    Admission getAdmission(Long admissionId);

    List<Admission> getAllAdmissions();

    // Bed availability dashboard
    Map<String, Object> getBedAvailabilitySummary();

    List<Map<String, Object>> getWardWiseBedSummary();
    
    void addWard(String wardName);
    List<String> getAllWardNames();
}
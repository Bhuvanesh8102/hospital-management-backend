package com.hospital.service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.entity.Admission;
import com.hospital.entity.Bed;
import com.hospital.entity.Room;

import com.hospital.repository.AdmissionRepository;
import com.hospital.repository.BedRepository;
import com.hospital.repository.RoomRepository;

@Service
public class BedInchargeServiceImpl implements BedInchargeService {

    private static final String WARD_MARKER = "__WARD__";

    private final BedRepository bedRepository;
    private final AdmissionRepository admissionRepository;
    private final RoomRepository roomRepository;

    public BedInchargeServiceImpl(
            BedRepository bedRepository,
            AdmissionRepository admissionRepository,
            RoomRepository roomRepository) {

        this.bedRepository = bedRepository;
        this.admissionRepository = admissionRepository;
        this.roomRepository = roomRepository;
    }

    // ================= BED MANAGEMENT =================

    @Override
    public List<Bed> getAllBeds() {
        return bedRepository.findAll();
    }

    @Override
    public List<Bed> getAvailableBeds() {
        return bedRepository.findByStatus("AVAILABLE");
    }

    @Override
    public List<Bed> getBedsByWard(String wardName) {
        return bedRepository.findByWardName(wardName);
    }

    @Override
    @Transactional
    public Bed addBed(Bed bed) {

        Room room = roomRepository
                .findByWardNameAndRoomNumber(
                        bed.getWardName(), bed.getRoomNumber())
                .orElseThrow(() ->
                        new RuntimeException("Room not found in the selected ward"));

        long currentBeds = bedRepository.countByWardNameAndRoomNumber(
                bed.getWardName(), bed.getRoomNumber());

        if (currentBeds >= room.getCapacity()) {
            throw new RuntimeException(
                    "Room capacity is full. Cannot add more beds");
        }

        bed.setStatus("AVAILABLE");

        return bedRepository.save(bed);
    }

    @Override
    @Transactional
    public Bed updateBed(Long bedId, Bed bed) {

        Bed existingBed = bedRepository.findById(bedId)
                .orElseThrow(() ->
                        new RuntimeException("Bed not found"));

        Room room = roomRepository
                .findByWardNameAndRoomNumber(
                        bed.getWardName(), bed.getRoomNumber())
                .orElseThrow(() ->
                        new RuntimeException("Room not found in the selected ward"));

        boolean movingToAnotherRoom =
                !existingBed.getWardName().equals(bed.getWardName())
                || !existingBed.getRoomNumber().equals(bed.getRoomNumber());

        if (movingToAnotherRoom) {

            long currentBeds = bedRepository.countByWardNameAndRoomNumber(
                    bed.getWardName(), bed.getRoomNumber());

            if (currentBeds >= room.getCapacity()) {
                throw new RuntimeException(
                        "Selected room has reached its capacity");
            }
        }

        existingBed.setWardName(bed.getWardName());
        existingBed.setRoomNumber(bed.getRoomNumber());
        existingBed.setBedNumber(bed.getBedNumber());
        existingBed.setBedType(bed.getBedType());
        existingBed.setStatus(bed.getStatus());

        return bedRepository.save(existingBed);
    }

    @Override
    public Bed assignBed(Long admissionId, Long bedId) {

        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() ->
                        new RuntimeException("Admission not found"));

        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() ->
                        new RuntimeException("Bed not found"));

        if (!"AVAILABLE".equalsIgnoreCase(bed.getStatus())) {
            throw new RuntimeException("Bed is not available");
        }

        bed.setStatus("OCCUPIED");
        bedRepository.save(bed);

        admission.setBed(bed);
        admissionRepository.save(admission);

        return bed;
    }

    @Override
    public Bed releaseBed(Long bedId) {

        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() ->
                        new RuntimeException("Bed not found"));

        bed.setStatus("AVAILABLE");

        return bedRepository.save(bed);
    }

    @Override
    public Admission transferBed(Long admissionId, Long newBedId) {

        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() ->
                        new RuntimeException("Admission not found"));

        Bed newBed = bedRepository.findById(newBedId)
                .orElseThrow(() ->
                        new RuntimeException("New bed not found"));

        if (!"AVAILABLE".equalsIgnoreCase(newBed.getStatus())) {
            throw new RuntimeException("New bed is not available");
        }

        Bed oldBed = admission.getBed();

        if (oldBed != null) {
            oldBed.setStatus("AVAILABLE");
            bedRepository.save(oldBed);
        }

        newBed.setStatus("OCCUPIED");
        bedRepository.save(newBed);

        admission.setBed(newBed);

        return admissionRepository.save(admission);
    }

    // ================= WARD MANAGEMENT =================

    @Override
    public void addWard(String wardName) {

        if (wardName == null || wardName.isBlank()) {
            throw new RuntimeException("Ward name is required");
        }

        wardName = wardName.trim();

        if (roomRepository.existsByWardName(wardName)) {
            throw new RuntimeException("Ward already exists");
        }

        Room wardRecord = new Room();

        wardRecord.setWardName(wardName);
        wardRecord.setRoomNumber(WARD_MARKER);
        wardRecord.setCapacity(0);

        roomRepository.save(wardRecord);
    }

    // ================= ROOM MANAGEMENT =================

    @Override
    public List<Map<String, Object>> getAllRooms() {

        List<Room> rooms = roomRepository.findAll()
                .stream()
                .filter(room -> !WARD_MARKER.equals(room.getRoomNumber()))
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Room room : rooms) {
            result.add(buildRoomDetails(room));
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getRoomsByWard(String wardName) {

        List<Room> rooms = roomRepository.findByWardName(wardName)
                .stream()
                .filter(room -> !WARD_MARKER.equals(room.getRoomNumber()))
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();

        for (Room room : rooms) {
            result.add(buildRoomDetails(room));
        }

        return result;
    }

    private Map<String, Object> buildRoomDetails(Room room) {

        long bedsAdded = bedRepository.countByWardNameAndRoomNumber(
                room.getWardName(), room.getRoomNumber());

        long availableCapacity = room.getCapacity() - bedsAdded;

        Map<String, Object> details = new LinkedHashMap<>();

        details.put("id", room.getId());
        details.put("wardName", room.getWardName());
        details.put("roomNumber", room.getRoomNumber());
        details.put("capacity", room.getCapacity());
        details.put("bedsAdded", bedsAdded);
        details.put("availableCapacity", availableCapacity);

        return details;
    }

    @Override
    public Room addRoom(Room room) {

        if (room.getWardName() == null || room.getWardName().isBlank()
                || room.getRoomNumber() == null || room.getRoomNumber().isBlank()) {
            throw new RuntimeException(
                    "Ward name and room number are required");
        }

        if (room.getCapacity() == null || room.getCapacity() <= 0) {
            throw new RuntimeException(
                    "Room capacity must be greater than zero");
        }

        room.setWardName(room.getWardName().trim());
        room.setRoomNumber(room.getRoomNumber().trim());

        if (WARD_MARKER.equals(room.getRoomNumber())) {
            throw new RuntimeException("Invalid room number");
        }

        if (roomRepository.existsByWardNameAndRoomNumber(
                room.getWardName(), room.getRoomNumber())) {
            throw new RuntimeException(
                    "This room already exists in the selected ward");
        }

        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public Room updateRoom(Long roomId, Room room) {

        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        if (WARD_MARKER.equals(existingRoom.getRoomNumber())) {
            throw new RuntimeException("Invalid room record");
        }

        if (room.getCapacity() == null || room.getCapacity() <= 0) {
            throw new RuntimeException(
                    "Room capacity must be greater than zero");
        }

        long currentBeds = bedRepository.countByWardNameAndRoomNumber(
                existingRoom.getWardName(), existingRoom.getRoomNumber());

        if (room.getCapacity() < currentBeds) {
            throw new RuntimeException(
                    "Capacity cannot be less than the number of beds already added");
        }

        String newWardName = room.getWardName().trim();
        String newRoomNumber = room.getRoomNumber().trim();

        if (WARD_MARKER.equals(newRoomNumber)) {
            throw new RuntimeException("Invalid room number");
        }

        boolean roomNameChanged =
                !existingRoom.getWardName().equals(newWardName)
                || !existingRoom.getRoomNumber().equals(newRoomNumber);

        if (roomNameChanged && currentBeds > 0) {
            throw new RuntimeException(
                    "Cannot change ward or room number after beds have been added");
        }

        if (roomRepository.existsByWardNameAndRoomNumberAndIdNot(
                newWardName, newRoomNumber, roomId)) {
            throw new RuntimeException(
                    "This room already exists in the selected ward");
        }

        existingRoom.setWardName(newWardName);
        existingRoom.setRoomNumber(newRoomNumber);
        existingRoom.setCapacity(room.getCapacity());

        return roomRepository.save(existingRoom);
    }

    // ================= ADMISSION MANAGEMENT =================

    @Override
    public Admission getAdmission(Long admissionId) {

        return admissionRepository.findById(admissionId)
                .orElseThrow(() ->
                        new RuntimeException("Admission not found"));
    }

    @Override
    public List<Admission> getAllAdmissions() {
        return admissionRepository.findAll();
    }

    // ================= BED AVAILABILITY DASHBOARD =================

    @Override
    public Map<String, Object> getBedAvailabilitySummary() {

        List<Bed> beds = bedRepository.findAll();

        long totalBeds = beds.size();

        long availableBeds = beds.stream()
                .filter(b -> "AVAILABLE".equalsIgnoreCase(b.getStatus()))
                .count();

        long occupiedBeds = beds.stream()
                .filter(b -> "OCCUPIED".equalsIgnoreCase(b.getStatus()))
                .count();

        long maintenanceBeds = beds.stream()
                .filter(b -> "MAINTENANCE".equalsIgnoreCase(b.getStatus()))
                .count();

        Map<String, Object> summary = new LinkedHashMap<>();

        summary.put("totalBeds", totalBeds);
        summary.put("availableBeds", availableBeds);
        summary.put("occupiedBeds", occupiedBeds);
        summary.put("maintenanceBeds", maintenanceBeds);

        return summary;
    }

    @Override
    public List<Map<String, Object>> getWardWiseBedSummary() {

        List<Bed> beds = bedRepository.findAll();

        Map<String, List<Bed>> wardGroups = new LinkedHashMap<>();

        for (Bed bed : beds) {

            String wardName = bed.getWardName();

            if (wardName == null || wardName.isBlank()) {
                wardName = "Unassigned";
            }

            wardGroups.computeIfAbsent(
                    wardName, key -> new ArrayList<>()).add(bed);
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, List<Bed>> entry : wardGroups.entrySet()) {

            List<Bed> wardBeds = entry.getValue();

            long total = wardBeds.size();

            long available = wardBeds.stream()
                    .filter(b -> "AVAILABLE".equalsIgnoreCase(b.getStatus()))
                    .count();

            long occupied = wardBeds.stream()
                    .filter(b -> "OCCUPIED".equalsIgnoreCase(b.getStatus()))
                    .count();

            long maintenance = wardBeds.stream()
                    .filter(b -> "MAINTENANCE".equalsIgnoreCase(b.getStatus()))
                    .count();

            Map<String, Object> wardSummary = new LinkedHashMap<>();

            wardSummary.put("wardName", entry.getKey());
            wardSummary.put("totalBeds", total);
            wardSummary.put("availableBeds", available);
            wardSummary.put("occupiedBeds", occupied);
            wardSummary.put("maintenanceBeds", maintenance);

            result.add(wardSummary);
        }

        return result;
    }
    @Override
    public List<String> getAllWardNames() {
        return roomRepository.findAllWardNames();
    }
}
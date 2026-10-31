package com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospital.entity.Admission;
import com.hospital.entity.Bed;
import com.hospital.repository.AdmissionRepository;
import com.hospital.repository.BedRepository;

@Service
public class BedInchargeServiceImpl implements BedInchargeService {

    private final BedRepository bedRepository;
    private final AdmissionRepository admissionRepository;

    public BedInchargeServiceImpl(
            BedRepository bedRepository,
            AdmissionRepository admissionRepository) {
        this.bedRepository = bedRepository;
        this.admissionRepository = admissionRepository;
    }

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
    public Bed addBed(Bed bed) {
        bed.setStatus("AVAILABLE");
        return bedRepository.save(bed);
    }

    @Override
    public Bed updateBed(Long bedId, Bed bed) {

        Bed existingBed = bedRepository.findById(bedId)
                .orElseThrow(() -> new RuntimeException("Bed not found"));

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
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new RuntimeException("Bed not found"));

        if (!"AVAILABLE".equals(bed.getStatus())) {
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
                .orElseThrow(() -> new RuntimeException("Bed not found"));

        bed.setStatus("AVAILABLE");

        return bedRepository.save(bed);
    }

    @Override
    public Admission transferBed(Long admissionId, Long newBedId) {

        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        Bed newBed = bedRepository.findById(newBedId)
                .orElseThrow(() -> new RuntimeException("New bed not found"));

        if (!"AVAILABLE".equals(newBed.getStatus())) {
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

    @Override
    public Admission getAdmission(Long admissionId) {

        return admissionRepository.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
    }
    
    @Override
    public List<Admission> getAllAdmissions() {
        return admissionRepository.findAll();
    }
}
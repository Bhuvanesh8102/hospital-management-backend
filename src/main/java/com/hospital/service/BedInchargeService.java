package com.hospital.service;

import java.util.List;

import com.hospital.entity.Admission;
import com.hospital.entity.Bed;

public interface BedInchargeService {

    List<Bed> getAllBeds();

    List<Bed> getAvailableBeds();

    List<Bed> getBedsByWard(String wardName);

    Bed addBed(Bed bed);

    Bed updateBed(Long bedId, Bed bed);

    Bed assignBed(Long admissionId, Long bedId);

    Bed releaseBed(Long bedId);

    Admission transferBed(Long admissionId, Long newBedId);

    Admission getAdmission(Long admissionId);
    
    List<Admission> getAllAdmissions();
}
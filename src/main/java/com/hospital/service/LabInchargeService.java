package com.hospital.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.hospital.entity.LabTest;

public interface LabInchargeService {

    List<LabTest> getPendingTests();

    List<LabTest> getAllTests();

    LabTest getTestById(Long testId);

    LabTest processTest(Long testId);

    LabTest enterTestResult(Long testId, String result);

    LabTest uploadReport(Long testId, MultipartFile file);

    Resource downloadReport(Long testId);

    LabTest completeTest(Long testId);

    List<LabTest> getPatientLabTests(Long patientId);

    List<LabTest> getTestsByCategory(String testCategory);
}
package com.hospital.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hospital.entity.LabTest;
import com.hospital.repository.LabTestRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Service
public class LabInchargeServiceImpl implements LabInchargeService {

    private final LabTestRepository labTestRepository;

    private final String uploadDirectory = "uploads/lab-reports/";

    public LabInchargeServiceImpl(LabTestRepository labTestRepository) {
        this.labTestRepository = labTestRepository;
    }

    @Override
    public List<LabTest> getPendingTests() {
        return labTestRepository.findByStatus("PENDING");
    }

    @Override
    public List<LabTest> getAllTests() {
        return labTestRepository.findAll();
    }

    @Override
    public LabTest getTestById(Long testId) {
        return labTestRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Lab test not found"));
    }

    @Override
    public LabTest processTest(Long testId) {

        LabTest labTest = getTestById(testId);

        labTest.setStatus("IN_PROGRESS");
        labTest.setTestDate(LocalDate.now());

        return labTestRepository.save(labTest);
    }

    @Override
    public LabTest enterTestResult(Long testId, String result) {

        LabTest labTest = getTestById(testId);

        labTest.setResult(result);

        return labTestRepository.save(labTest);
    }

    @Override
    public LabTest uploadReport(Long testId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select a report file");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null ||
                !originalFileName.toLowerCase().endsWith(".pdf")) {

            throw new RuntimeException("Only PDF files are allowed");
        }

        try {

            Path uploadPath = Paths.get(uploadDirectory);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = "LAB_" + testId + "_" + System.currentTimeMillis()
                    + ".pdf";

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath);

            LabTest labTest = getTestById(testId);

            labTest.setReportFilePath(filePath.toString());

            return labTestRepository.save(labTest);

        } catch (IOException e) {

            throw new RuntimeException("Failed to upload lab report");
        }
    }

    @Override
    public LabTest completeTest(Long testId) {

        LabTest labTest = getTestById(testId);

        labTest.setStatus("COMPLETED");
        labTest.setReportDate(LocalDate.now());

        return labTestRepository.save(labTest);
    }

    @Override
    public List<LabTest> getPatientLabTests(Long patientId) {
        return labTestRepository.findByPatientId(patientId);
    }

    @Override
    public List<LabTest> getTestsByCategory(String testCategory) {
        return labTestRepository.findByTestCategory(testCategory);
    }
    @Override
    public Resource downloadReport(Long testId) {

        LabTest labTest = getTestById(testId);

        if (labTest.getReportFilePath() == null) {
            throw new RuntimeException("Lab report not available");
        }

        try {

            Path filePath = Paths.get(labTest.getReportFilePath());

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Lab report file not found");
            }

            return resource;

        } catch (Exception e) {

            throw new RuntimeException("Unable to download lab report");
        }
    }
}

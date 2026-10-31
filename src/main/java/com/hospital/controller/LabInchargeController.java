package com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hospital.entity.LabTest;
import com.hospital.service.LabInchargeService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/lab")
public class LabInchargeController {

    private final LabInchargeService labInchargeService;

    public LabInchargeController(LabInchargeService labInchargeService) {
        this.labInchargeService = labInchargeService;
    }

    // View pending laboratory tests
    @GetMapping("/tests/pending")
    public ResponseEntity<List<LabTest>> getPendingTests() {
        return ResponseEntity.ok(
                labInchargeService.getPendingTests());
    }

    // View all laboratory tests
    @GetMapping("/tests")
    public ResponseEntity<List<LabTest>> getAllTests() {
        return ResponseEntity.ok(
                labInchargeService.getAllTests());
    }

    // View test by ID
    @GetMapping("/test/{testId}")
    public ResponseEntity<?> getTestById(
            @PathVariable Long testId) {

        try {
            return ResponseEntity.ok(
                    labInchargeService.getTestById(testId));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Start processing test
    @PutMapping("/test/{testId}/process")
    public ResponseEntity<?> processTest(
            @PathVariable Long testId) {

        try {
            return ResponseEntity.ok(
                    labInchargeService.processTest(testId));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Enter test result
    @PutMapping("/test/{testId}/result")
    public ResponseEntity<?> enterTestResult(
            @PathVariable Long testId,
            @RequestParam String result) {

        try {
            return ResponseEntity.ok(
                    labInchargeService.enterTestResult(
                            testId, result));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Upload report
 // Upload PDF laboratory report
    @PostMapping(
            value = "/test/{testId}/report",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<?> uploadReport(
            @PathVariable Long testId,
            @RequestParam("file") MultipartFile file) {

        try {

            LabTest updatedLabTest =
                    labInchargeService.uploadReport(testId, file);

            return ResponseEntity.ok(updatedLabTest);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Complete test
    @PutMapping("/test/{testId}/complete")
    public ResponseEntity<?> completeTest(
            @PathVariable Long testId) {

        try {
            return ResponseEntity.ok(
                    labInchargeService.completeTest(testId));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // View patient's lab tests
    @GetMapping("/patient/{patientId}/tests")
    public ResponseEntity<List<LabTest>> getPatientLabTests(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                labInchargeService.getPatientLabTests(patientId));
    }

    // View tests by category
    @GetMapping("/tests/category/{testCategory}")
    public ResponseEntity<List<LabTest>> getTestsByCategory(
            @PathVariable String testCategory) {

        return ResponseEntity.ok(
                labInchargeService.getTestsByCategory(testCategory));
    }
 // Download/View laboratory report
    @GetMapping("/test/{testId}/report")
    public ResponseEntity<?> downloadReport(@PathVariable Long testId) {

        try {

            Resource resource = labInchargeService.downloadReport(testId);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\""
                    )
                    .header(
                            HttpHeaders.CONTENT_TYPE,
                            "application/pdf"
                    )
                    .body(resource);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
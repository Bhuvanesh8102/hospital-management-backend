package com.hospital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.Medicine;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByMedicineNameContainingIgnoreCase(
            String medicineName);

    Optional<Medicine> findByBatchNumber(String batchNumber);

    List<Medicine> findByStatus(String status);

    List<Medicine> findByQuantityLessThan(Integer quantity);
}
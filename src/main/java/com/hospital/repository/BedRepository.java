package com.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.Bed;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {

    List<Bed> findByStatus(String status);

    List<Bed> findByWardName(String wardName);

    List<Bed> findByWardNameAndRoomNumber(
            String wardName, String roomNumber);

    long countByWardNameAndRoomNumber(
            String wardName, String roomNumber);
}
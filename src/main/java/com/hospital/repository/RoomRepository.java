
package com.hospital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hospital.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByWardName(String wardName);

    Optional<Room> findByWardNameAndRoomNumber(String wardName, String roomNumber);

    boolean existsByWardNameAndRoomNumber(String wardName, String roomNumber);

    boolean existsByWardNameAndRoomNumberAndIdNot(
        String wardName, String roomNumber, Long id
    );

    boolean existsByWardName(String wardName);

    @Query("SELECT DISTINCT r.wardName FROM Room r ORDER BY r.wardName")
    List<String> findAllWardNames();
}

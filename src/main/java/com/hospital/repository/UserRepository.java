package com.hospital.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hospital.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByMobile(String mobile);

    Optional<User> findByUhid(String uhid);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByUhid(String uhid);

    List<User> findByRole(String role);

    List<User> findByRoleAndStatus(String role, String status);

    List<User> findByStatus(String status);
}
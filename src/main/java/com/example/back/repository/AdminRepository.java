package com.example.back.repository;

import java.util.Optional;

import com.example.back.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByIdAndDeletionDateIsNull(Long id);

    Optional<Admin> findByMailIgnoreCaseAndDeletionDateIsNull(String mail);
}

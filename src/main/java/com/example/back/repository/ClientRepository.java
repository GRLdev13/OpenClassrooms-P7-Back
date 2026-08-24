package com.example.back.repository;

import java.util.List;
import java.util.Optional;

import com.example.back.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByDeletionDateIsNullOrderByIdAsc();

    Optional<Client> findByIdAndDeletionDateIsNull(Long id);

    boolean existsByMailIgnoreCase(String mail);

    boolean existsByMailIgnoreCaseAndIdNot(String mail, Long id);
}

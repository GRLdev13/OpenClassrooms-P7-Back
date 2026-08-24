package com.example.back.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.example.back.domain.Client;

public record ClientDto(
        Long id,
        String mail,
        String firstName,
        String lastName,
        String phone,
        LocalDate birthday,
        String address,
        Instant creationDate,
        Instant deletionDate,
        Long modifiedBy,
        Instant modificationDate) {

    public static ClientDto fromEntity(Client client) {
        return new ClientDto(
                client.getId(),
                client.getMail(),
                client.getFirstName(),
                client.getLastName(),
                client.getPhone(),
                client.getBirthday(),
                client.getAddress(),
                client.getCreationDate(),
                client.getDeletionDate(),
                client.getModifiedBy(),
                client.getModificationDate());
    }
}

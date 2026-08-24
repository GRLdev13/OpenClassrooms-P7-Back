package com.example.back.dto;

import java.time.Instant;

import com.example.back.domain.Admin;

public record AdminDto(
        Long id,
        Long roleId,
        String mail,
        String employeeCode,
        Instant creationDate,
        Instant deletionDate,
        Long modifiedBy,
        Instant modificationDate) {

    public static AdminDto fromEntity(Admin admin) {
        Long roleId = admin.getRole() == null ? null : admin.getRole().getId();

        return new AdminDto(
                admin.getId(),
                roleId,
                admin.getMail(),
                admin.getEmployeeCode(),
                admin.getCreationDate(),
                admin.getDeletionDate(),
                admin.getModifiedBy(),
                admin.getModificationDate());
    }
}

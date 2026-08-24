package com.example.back.dto;

import com.example.back.domain.Role;

public record RoleDto(Long id, String name) {

    public static RoleDto fromEntity(Role role) {
        return new RoleDto(role.getId(), role.getName());
    }
}

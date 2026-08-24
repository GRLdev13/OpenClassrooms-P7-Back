package com.example.back.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDto(
        @NotBlank @Email String email,
        @NotBlank @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) String token) {
}

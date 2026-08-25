package com.example.back.controller;

import java.util.List;

import com.example.back.dto.ClientDto;
import com.example.back.dto.CreateUserRequest;
import com.example.back.dto.LoginDto;
import com.example.back.dto.UpdateUserRequest;
import com.example.back.service.UserManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Manage client accounts")
public class UserController {

    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping
    @Operation(summary = "List active users")
    public List<ClientDto> getAll() {
        return userManager.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an active user by ID")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ClientDto getById(@PathVariable Long id) {
        return userManager.getById(id);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in an active user")
    @ApiResponse(responseCode = "200", description = "Credentials accepted")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Invalid email or password")
    public LoginDto login(
            @Valid @RequestBody LoginDto request) {
        return userManager.login(request);
    }

    @PostMapping("/admin/login")
    @Operation(summary = "Log in an admin")
    @ApiResponse(responseCode = "200", description = "Credentials accepted")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Invalid email or password")
    public LoginDto loginAdmin(
            @Valid @RequestBody LoginDto request) {
        return userManager.loginAdmin(request);
    }

     @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a user")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "409", description = "Email already used")
    public ClientDto create(@Valid @RequestBody CreateUserRequest request) {
        return userManager.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "409", description = "Email already used")
    public ClientDto update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userManager.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft-delete a user")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "User not found")
    public void delete(@PathVariable Long id) {
        userManager.delete(id);
    }
}

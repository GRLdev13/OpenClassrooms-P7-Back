package com.example.back.controller;

import com.example.back.domain.Admin;
import com.example.back.domain.Client;
import com.example.back.repository.AdminRepository;
import com.example.back.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void performsTheUserCrudLifecycle() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mail": "jane@example.com",
                                  "password": "safe-password",
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "phone": "+33123456789",
                                  "birthday": "1990-05-12",
                                  "address": "1 Main Street"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mail").value("jane@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        Client client = clientRepository.findAll().getFirst();
        assertThat(client.getPassword()).isNotEqualTo("safe-password");
        assertThat(passwordEncoder.matches("safe-password", client.getPassword())).isTrue();

        mockMvc.perform(get("/api/users/{id}", client.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));

        mockMvc.perform(put("/api/users/{id}", client.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mail": "jane@example.com",
                                  "firstName": "Janet",
                                  "lastName": "Doe",
                                  "phone": "+33123456789",
                                  "birthday": "1990-05-12",
                                  "address": "2 Main Street"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Janet"));

        mockMvc.perform(delete("/api/users/{id}", client.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", client.getId()))
                .andExpect(status().isNotFound());

        assertThat(clientRepository.findById(client.getId()))
                .get()
                .extracting(Client::getDeletionDate)
                .isNotNull();
    }

    @Test
    void logsInWithValidCredentialsWithoutEchoingThePassword() throws Exception {
        Client client = new Client();
        client.setMail("login@example.com");
        client.setPassword(passwordEncoder.encode("correct-password"));
        clientRepository.save(client);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "LOGIN@example.com",
                                  "password": "correct-password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("login@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void rejectsInvalidLoginCredentials() throws Exception {
        Client client = new Client();
        client.setMail("secured@example.com");
        client.setPassword(passwordEncoder.encode("correct-password"));
        clientRepository.save(client);

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "secured@example.com",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "unknown@example.com",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logsInAnAdminAgainstTheAdminTable() throws Exception {
        Admin admin = new Admin();
        admin.setMail("admin-login@example.com");
        admin.setPassword(passwordEncoder.encode("admin-password"));
        adminRepository.save(admin);

        mockMvc.perform(post("/api/users/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ADMIN-LOGIN@example.com",
                                  "password": "admin-password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin-login@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void doesNotAuthenticateAClientThroughTheAdminLogin() throws Exception {
        Client client = new Client();
        client.setMail("client-only@example.com");
        client.setPassword(passwordEncoder.encode("client-password"));
        clientRepository.save(client);

        mockMvc.perform(post("/api/users/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "client-only@example.com",
                                  "password": "client-password"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void upgradesTheSeededPlaintextAdminPasswordAfterLogin() throws Exception {
        Admin admin = new Admin();
        admin.setMail("legacy-admin@example.com");
        admin.setPassword("legacy-password");
        adminRepository.saveAndFlush(admin);

        mockMvc.perform(post("/api/users/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "legacy-admin@example.com",
                                  "password": "legacy-password"
                                }
                                """))
                .andExpect(status().isOk());

        adminRepository.flush();
        assertThat(admin.getPassword()).isNotEqualTo("legacy-password");
        assertThat(passwordEncoder.matches("legacy-password", admin.getPassword())).isTrue();
    }
}

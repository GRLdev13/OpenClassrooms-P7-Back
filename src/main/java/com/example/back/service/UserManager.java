package com.example.back.service;

import java.time.Instant;
import java.util.List;

import com.example.back.domain.Admin;
import com.example.back.domain.Client;
import com.example.back.dto.ClientDto;
import com.example.back.dto.CreateUserRequest;
import com.example.back.dto.LoginDto;
import com.example.back.dto.UpdateUserRequest;
import com.example.back.exception.EmailAlreadyUsedException;
import com.example.back.exception.InvalidCredentialsException;
import com.example.back.exception.UserNotFoundException;
import com.example.back.repository.AdminRepository;
import com.example.back.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserManager {

    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManager(
            ClientRepository clientRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<ClientDto> getAll() {
        return clientRepository.findAllByDeletionDateIsNullOrderByIdAsc().stream()
                .map(ClientDto::fromEntity)
                .toList();
    }

    public ClientDto getById(Long id) {
        return ClientDto.fromEntity(findActiveClient(id));
    }

    public LoginDto login(LoginDto request) {
        Client client = clientRepository
                .findByMailIgnoreCaseAndDeletionDateIsNull(request.email())
                .filter(candidate -> passwordEncoder.matches(
                        request.password(), candidate.getPassword()))
                .orElseThrow(InvalidCredentialsException::new);

        return new LoginDto(client.getMail(), null, "");
    }

    @Transactional
    public LoginDto loginAdmin(LoginDto request) {
        Admin admin = adminRepository
                .findByMailIgnoreCaseAndDeletionDateIsNull(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!matchesAdminPassword(request.password(), admin.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if (!admin.getPassword().startsWith("{")) {
            admin.setPassword(passwordEncoder.encode(request.password()));
            admin.setModificationDate(Instant.now());
        }

        return new LoginDto(admin.getMail(), null, "");
    }

    @Transactional
    public ClientDto create(CreateUserRequest request) {
        if (clientRepository.existsByMailIgnoreCase(request.mail())) {
            throw new EmailAlreadyUsedException(request.mail());
        }

        Instant now = Instant.now();
        Client client = new Client();
        client.setMail(request.mail());
        client.setPassword(passwordEncoder.encode(request.password()));
        client.setFirstName(request.firstName());
        client.setLastName(request.lastName());
        client.setPhone(request.phone());
        client.setBirthday(request.birthday());
        client.setAddress(request.address());
        client.setCreationDate(now);
        client.setModificationDate(now);

        return ClientDto.fromEntity(clientRepository.save(client));
    }

    @Transactional
    public ClientDto update(Long id, UpdateUserRequest request) {
        Client client = findActiveClient(id);
        if (clientRepository.existsByMailIgnoreCaseAndIdNot(request.mail(), id)) {
            throw new EmailAlreadyUsedException(request.mail());
        }

        client.setMail(request.mail());
        client.setFirstName(request.firstName());
        client.setLastName(request.lastName());
        client.setPhone(request.phone());
        client.setBirthday(request.birthday());
        client.setAddress(request.address());
        client.setModificationDate(Instant.now());

        if (request.password() != null && !request.password().isBlank()) {
            client.setPassword(passwordEncoder.encode(request.password()));
        }

        return ClientDto.fromEntity(client);
    }

    @Transactional
    public void delete(Long id) {
        Client client = findActiveClient(id);
        Instant now = Instant.now();
        client.setDeletionDate(now);
        client.setModificationDate(now);
    }

    private Client findActiveClient(Long id) {
        return clientRepository.findByIdAndDeletionDateIsNull(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private boolean matchesAdminPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (!storedPassword.startsWith("{")) {
            return storedPassword.equals(rawPassword);
        }
        return passwordEncoder.matches(rawPassword, storedPassword);
    }
}

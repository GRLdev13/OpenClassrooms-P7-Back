package com.example.back.service;

import java.time.Instant;
import java.util.List;

import com.example.back.domain.Client;
import com.example.back.dto.ClientDto;
import com.example.back.dto.CreateUserRequest;
import com.example.back.dto.UpdateUserRequest;
import com.example.back.exception.EmailAlreadyUsedException;
import com.example.back.exception.UserNotFoundException;
import com.example.back.repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserManager {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManager(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
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
}

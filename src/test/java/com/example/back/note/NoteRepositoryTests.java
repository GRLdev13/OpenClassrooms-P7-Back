package com.example.back.note;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class NoteRepositoryTests {

    @Autowired
    private NoteRepository repository;

    @Test
    void persistsAndReadsANote() {
        Note saved = repository.saveAndFlush(new Note("Hibernate is configured"));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId()))
                .get()
                .extracting(Note::getContent)
                .isEqualTo("Hibernate is configured");
    }
}

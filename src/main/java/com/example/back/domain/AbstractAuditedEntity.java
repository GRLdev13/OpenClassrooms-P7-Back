package com.example.back.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractAuditedEntity extends AbstractEntity {

    @Column(name = "creation_date")
    private Instant creationDate;

    @Column(name = "deletion_date")
    private Instant deletionDate;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "modification_date")
    private Instant modificationDate;

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(Instant deletionDate) {
        this.deletionDate = deletionDate;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Instant modificationDate) {
        this.modificationDate = modificationDate;
    }
}

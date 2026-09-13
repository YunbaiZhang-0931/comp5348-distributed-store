package edu.usyd.comp5348.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Column(nullable = false, updatable = false)
    protected Instant createdAt;
    @Column(nullable = false)
    protected Instant updatedAt;
    @PrePersist
    protected void onCreate() {
        var now = Instant.now();
        createdAt = now; updatedAt = now;
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

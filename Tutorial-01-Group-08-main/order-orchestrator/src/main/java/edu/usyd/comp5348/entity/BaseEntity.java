package edu.usyd.comp5348.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Column(nullable=false, updatable=false) protected Instant createdAt;
    @Column(nullable=false) protected Instant updatedAt;
    @PrePersist void onCreate(){ var now=Instant.now(); createdAt=now; updatedAt=now; }
    @PreUpdate  void onUpdate(){ updatedAt=Instant.now(); }
}
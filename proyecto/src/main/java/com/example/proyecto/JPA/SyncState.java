package com.example.proyecto.JPA;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_state")
public class SyncState {

    @Id
    private String id;

    private LocalDateTime lastSuccessfulSyncAt;

    public SyncState() {}

    public SyncState(String id, LocalDateTime lastSuccessfulSyncAt) {
        this.id = id;
        this.lastSuccessfulSyncAt = lastSuccessfulSyncAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getLastSuccessfulSyncAt() { return lastSuccessfulSyncAt; }
    public void setLastSuccessfulSyncAt(LocalDateTime lastSuccessfulSyncAt) {
        this.lastSuccessfulSyncAt = lastSuccessfulSyncAt;
    }
}

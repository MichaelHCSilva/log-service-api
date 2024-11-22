package com.logservice.models;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import com.logservice.enums.LogLevel;

@Entity
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private LogLevel level;

    private String message;

    @Column(name = "additional_data", columnDefinition = "TEXT")
    @Convert(converter = AdditionalDataConverter.class)
    private Map<String, Object> additionalData;

    private ZonedDateTime timestamp;

    // Getters e setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

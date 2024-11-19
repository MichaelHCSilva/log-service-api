package com.logservice.models;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.logservice.enums.LogLevel;

@Entity
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private LogLevel level; 

    @Column(nullable = false)
    private String message;

    private ZonedDateTime timestamp;

    @Lob
    private String additionalData; 

    public LogEntry() {
        
        this.timestamp = ZonedDateTime.now();
    }

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

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }
}
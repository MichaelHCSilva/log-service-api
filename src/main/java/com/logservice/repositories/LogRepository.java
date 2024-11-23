package com.logservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logservice.enums.LogLevel;
import com.logservice.models.LogEntry;

public interface LogRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findByLevel(LogLevel level);
}

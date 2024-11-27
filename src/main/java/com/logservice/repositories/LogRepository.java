package com.logservice.repositories;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logservice.enums.LogLevel;
import com.logservice.models.LogEntry;

public interface LogRepository extends JpaRepository<LogEntry, Long> {

    List<LogEntry> findByLevel(LogLevel level);

    @Query("SELECT l FROM LogEntry l WHERE l.level = :level AND l.timestamp BETWEEN :startDate AND :endDate")
    List<LogEntry> findLogsByLevelAndDateRange(@Param("level") LogLevel level,
                                                @Param("startDate") OffsetDateTime startDate,
                                                @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT l FROM LogEntry l WHERE l.timestamp BETWEEN :startDate AND :endDate")
    List<LogEntry> findLogsByDateRange(@Param("startDate") OffsetDateTime startDate,
                                       @Param("endDate") OffsetDateTime endDate);
}

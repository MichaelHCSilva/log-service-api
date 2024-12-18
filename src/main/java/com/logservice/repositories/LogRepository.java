package com.logservice.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.logservice.enums.LogNivel;
import com.logservice.models.LogEntry;

public interface LogRepository extends JpaRepository<LogEntry, UUID> {

    List<LogEntry> findByNivel(LogNivel nivel);

    @Query("SELECT l FROM LogEntry l WHERE l.nivel = :nivel AND l.timestamp BETWEEN :startDate AND :endDate")
    List<LogEntry> findLogsByNivelAndDateRange(@Param("nivel") LogNivel nivel,
                                                @Param("startDate") OffsetDateTime startDate,
                                                @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT l FROM LogEntry l WHERE l.timestamp BETWEEN :startDate AND :endDate")
    List<LogEntry> findLogsByDateRange(@Param("startDate") OffsetDateTime startDate,
                                       @Param("endDate") OffsetDateTime endDate);
}

package com.logservice.services;

import com.logservice.dtos.LogEntryDTO;
import com.logservice.models.LogEntry;
import com.logservice.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public LogEntry saveLog(LogEntryDTO logEntryDTO) {
        LogEntry log = new LogEntry();
        log.setLevel(logEntryDTO.getLevel());
        log.setMessage(logEntryDTO.getMessage());
        log.setAdditionalData(logEntryDTO.getAdditionalData());
        log.setTimestamp(ZonedDateTime.now());
        return logRepository.save(log);
    }
}
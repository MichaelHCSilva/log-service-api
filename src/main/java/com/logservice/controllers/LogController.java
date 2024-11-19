package com.logservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logservice.dtos.LogEntryDTO;
import com.logservice.models.LogEntry;
import com.logservice.services.LogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @PostMapping
    public ResponseEntity<LogEntry> receiveLog(@Valid @RequestBody LogEntryDTO logEntryDTO) {
        LogEntry savedLog = logService.saveLog(logEntryDTO);
        return ResponseEntity.ok(savedLog);
    }
}
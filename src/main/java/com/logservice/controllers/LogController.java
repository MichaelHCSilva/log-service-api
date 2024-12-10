package com.logservice.controllers;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.logservice.dtos.LogEntryDTO;
import com.logservice.enums.LogLevel;
import com.logservice.models.LogEntry;
import com.logservice.services.LogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LogService logService;

    @PostMapping
    public ResponseEntity<LogEntry> receiveLog(@Valid @RequestBody LogEntryDTO logEntryDTO) {
        logger.info("Recebendo uma nova entrada de log...");
        logger.debug("Payload recebido: {}", logEntryDTO);

        LogEntry savedLog = logService.saveLog(logEntryDTO);

        logger.info("Log salvo com sucesso. ID: {}", savedLog.getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedLog.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedLog);
    }

    @GetMapping
    public ResponseEntity<List<LogEntry>> getLogs(
            @RequestParam(required = false) LogLevel level,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {
        try {
            if (level == null) {
                logger.warn("Nenhum nível de log foi especificado. Retornando logs sem filtro de nível.");
            } else {
                logger.info("Buscando logs. Filtro de nível: {}", level);
            }

            List<LogEntry> logs = logService.getLogs(level, startDate, endDate);

            if (logs.isEmpty()) {
                logger.warn("Nenhum log encontrado com os filtros fornecidos.");
            } else {
                logger.info("Total de logs encontrados: {}", logs.size());
            }

            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            logger.error("Erro ao buscar logs: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteLog(@PathVariable UUID id) {
        logger.info("Requisição para deletar log com ID: {}", id);
        Map<String, String> response = new HashMap<>();
        try {
            logService.deleteLogById(id);
            logger.info("Log com ID {} deletado com sucesso.", id);
            response.put("message", "Log com ID " + id + " deletado com sucesso.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Log não encontrado para o ID: {}", id);
            response.put("details", "Log não encontrado para o ID: " + id);
            return ResponseEntity.status(404).body(response);
        }
    }

}

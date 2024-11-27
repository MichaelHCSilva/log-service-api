package com.logservice.services;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logservice.dtos.LogEntryDTO;
import com.logservice.enums.LogLevel;
import com.logservice.models.LogEntry;
import com.logservice.repositories.LogRepository;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    @Autowired
    private LogRepository logRepository;

    public LogEntry saveLog(LogEntryDTO logEntryDTO) {
        logger.info("Iniciando o processo de salvar log...");

        LogEntry log = new LogEntry();
        log.setLevel(logEntryDTO.getLevel());
        log.setMessage(logEntryDTO.getMessage());
        logger.debug("Log básico criado com level: {} e mensagem: {}", logEntryDTO.getLevel(), logEntryDTO.getMessage());

        if (logEntryDTO.getAdditionalData() != null) {
            log.setAdditionalData(logEntryDTO.getAdditionalData());
            logger.debug("Dados adicionais atribuídos: {}", logEntryDTO.getAdditionalData());
        } else {
            logger.debug("Nenhum dado adicional fornecido.");
        }

        log.setTimestamp(OffsetDateTime.now());
        logger.info("Timestamp gerado automaticamente: {}", log.getTimestamp());

        LogEntry savedLog = logRepository.save(log);
        logger.info("Log salvo com sucesso no banco de dados. ID: {}", savedLog.getId());
        return savedLog;
    }

    public List<LogEntry> getLogs(LogLevel level, OffsetDateTime startDate, OffsetDateTime endDate) {
    if (level != null) {
        if (startDate != null && endDate != null) {
            return logRepository.findLogsByLevelAndDateRange(level, startDate, endDate);
        } else {
            return logRepository.findByLevel(level);
        }
    }
    if (startDate != null && endDate != null) {
        return logRepository.findLogsByDateRange(startDate, endDate);
    }
    return logRepository.findAll();
}


    public LogEntry getLogById(Long id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log não encontrado com ID: " + id));
    }
}

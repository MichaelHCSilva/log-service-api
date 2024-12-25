package com.logservice.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.logservice.dtos.LogEntryDTO;
import com.logservice.enums.LogNivel;
import com.logservice.models.LogEntry;
import com.logservice.repositories.LogRepository;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    @Autowired
    private LogRepository logRepository;

    public LogEntry saveLog(LogEntryDTO logEntryDTO) {
        logger.info("Iniciando o processo de salvar log...");

        if (logEntryDTO.getNivel() == null) {
            throw new IllegalArgumentException("Nível de log é obrigatório.");
        }

        if (logEntryDTO.getMessage() == null || logEntryDTO.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Mensagem de log é obrigatória.");
        }

        int maxMessageSize = Integer.parseInt(System.getenv("LOG_MAX_MESSAGE_SIZE")); // Lê do .env
        if (logEntryDTO.getMessage() != null && logEntryDTO.getMessage().length() > maxMessageSize) {
            throw new IllegalArgumentException("A mensagem de log excede o tamanho máximo permitido de " + maxMessageSize + " caracteres.");
        }

        LogEntry log = new LogEntry();
        log.setNivel(logEntryDTO.getNivel());
        log.setMessage(logEntryDTO.getMessage());
        logger.debug("Log básico criado com Nivel: {} e mensagem: {}", logEntryDTO.getNivel(), logEntryDTO.getMessage());

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

    public List<LogEntry> getLogs(LogNivel nivel, OffsetDateTime startDate, OffsetDateTime endDate) {
        if (nivel != null) {
            if (startDate != null && endDate != null) {
                return logRepository.findLogsByNivelAndDateRange(nivel, startDate, endDate);
            } else {
                return logRepository.findByNivel(nivel);
            }
        }
        if (startDate != null && endDate != null) {
            return logRepository.findLogsByDateRange(startDate, endDate);
        }
        return logRepository.findAll();
    }

    public void deleteLogById(UUID id) {
        logger.info("Deletando log com ID: {}", id);
        logRepository.deleteById(id);
        logger.info("Log com ID: {} deletado com sucesso", id);
    }
}

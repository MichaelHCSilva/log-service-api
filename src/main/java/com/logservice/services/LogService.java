package com.logservice.services;

import com.logservice.dtos.LogEntryDTO;
import com.logservice.models.LogEntry;
import com.logservice.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

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

        // Verifica e atribui os dados adicionais
        if (logEntryDTO.getAdditionalData() != null) {
            log.setAdditionalData(logEntryDTO.getAdditionalData()); // Atribui o mapa diretamente
            logger.debug("Dados adicionais atribuídos: {}", logEntryDTO.getAdditionalData());
        } else {
            logger.debug("Nenhum dado adicional fornecido.");
        }

        log.setTimestamp(ZonedDateTime.now());
        logger.info("Timestamp gerado automaticamente: {}", log.getTimestamp());

        // Salva o log no banco de dados
        LogEntry savedLog = logRepository.save(log);
        logger.info("Log salvo com sucesso no banco de dados. ID: {}", savedLog.getId());
        return savedLog;
    }
}

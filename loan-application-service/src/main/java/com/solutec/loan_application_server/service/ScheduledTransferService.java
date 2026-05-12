package com.solutec.loan_application_server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledTransferService {

    private final StoreItemService storeItemService;

    @Scheduled(cron = "0 0 2 * * *") // Cada día a las 2:00 AM
    public void transferOverdueItemsToStore() {
        try {
            storeItemService.transferOverdueLoansToStore();
            log.info("===== FIN: Proceso completado exitosamente =====");
        } catch (Exception e) {
            log.error("===== ERROR: Fallo en proceso de transferencia automática =====", e);
        }
    }


    // @Scheduled(fixedRate = 21600000) // Cada 6 horas (6 * 60 * 60 * 1000)
    public void transferOverdueItemsPeriodically() {
        log.info("Ejecutando verificación periódica de préstamos vencidos");
        storeItemService.transferOverdueLoansToStore();
    }
}
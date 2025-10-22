package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.entity.PaymentSchedule;
import com.solutec.loan_application_server.repository.PaymentScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledNotificationService {

    private final PaymentScheduleRepository paymentScheduleRepository;
    private final NotificationService notificationService;

    /**
     * Se ejecuta diariamente a las 9:00 AM
     * Envía notificaciones a clientes con pagos que vencen mañana
     */
    @Scheduled(cron = "0 0 9 * * *") // Diario a las 9:00 AM
    @Transactional
    public void sendPaymentReminders() {
        log.info("Iniciando envío de recordatorios de pago...");

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<PaymentSchedule> paymentsDueTomorrow =
                paymentScheduleRepository.findPaymentsDueTomorrow(tomorrow);

        log.info("Encontrados {} pagos que vencen mañana", paymentsDueTomorrow.size());

        for (PaymentSchedule schedule : paymentsDueTomorrow) {
            try {
                notificationService.notifyPaymentDue(
                        schedule.getLoan().getLoanApplication().getUser(),
                        schedule
                );

                // Marcar como notificado
                schedule.setNotificationSent(true);
                paymentScheduleRepository.save(schedule);

                log.info("Recordatorio enviado para pago #{} del préstamo {}",
                        schedule.getPaymentNumber(),
                        schedule.getLoan().getLoanId());

            } catch (Exception e) {
                log.error("Error enviando recordatorio para schedule {}",
                        schedule.getScheduleId(), e);
            }
        }

        log.info("Envío de recordatorios completado");
    }

    /**
     * Se ejecuta diariamente a las 10:00 AM
     * Envía notificaciones al cobrador sobre pagos vencidos (después de días de gracia)
     */
    @Scheduled(cron = "0 0 10 * * *") // Diario a las 10:00 AM
    @Transactional
    public void sendCollectorNotifications() {
        log.info("Iniciando envío de notificaciones a cobradores...");

        LocalDate today = LocalDate.now();

        List<PaymentSchedule> overduePayments =
                paymentScheduleRepository.findOverduePayments(today);

        log.info("Encontrados {} pagos vencidos sin notificar", overduePayments.size());

        for (PaymentSchedule schedule : overduePayments) {
            try {
                // Calcular días de gracia
                Integer gracePeriodDays = schedule.getLoan().getGracePeriodDays();
                if (gracePeriodDays == null) gracePeriodDays = 0;

                LocalDate dueDate = schedule.getDueDate();
                LocalDate graceEndDate = dueDate.plusDays(gracePeriodDays);

                // Solo notificar si ya pasaron los días de gracia
                if (today.isAfter(graceEndDate) || today.isEqual(graceEndDate)) {
                    notificationService.notifyCollectorOverdue(schedule);

                    // Marcar como notificado al cobrador
                    schedule.setCollectorNotified(true);
                    schedule.setStatus("VENCIDO");
                    paymentScheduleRepository.save(schedule);

                    log.info("Cobrador notificado sobre pago vencido #{} del préstamo {}",
                            schedule.getPaymentNumber(),
                            schedule.getLoan().getLoanId());
                }

            } catch (Exception e) {
                log.error("Error enviando notificación a cobrador para schedule {}",
                        schedule.getScheduleId(), e);
            }
        }

        log.info("Envío de notificaciones a cobradores completado");
    }
}
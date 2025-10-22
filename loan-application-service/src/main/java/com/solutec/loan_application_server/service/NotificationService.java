package com.solutec.loan_application_server.service;

import com.solutec.loan_application_server.entity.*;
import com.solutec.loan_application_server.repository.NotificationRepository;
import com.solutec.loan_application_server.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    /**
     * Notifica al cliente cuando reporta un pago
     */
    public void notifyPaymentReported(User user, Payment payment) {
        String message = String.format(
                "Tu reporte de pago #%d por Q%.2f ha sido recibido y está en revisión.",
                payment.getPaymentNumber(),
                payment.getAmountPaid()
        );

        createNotification(user, message, "PAYMENT_REPORTED", "PAYMENT", payment.getPaymentId());

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Pago Reportado - En Revisión",
                    buildPaymentReportedEmail(user, payment)
            );
        } catch (MessagingException e) {
            log.error("Error enviando email de pago reportado", e);
        }
    }

    /**
     * Notifica al cliente cuando su pago es revisado
     */
    public void notifyPaymentReviewed(User user, Payment payment) {
        String status = "APROBADO".equals(payment.getStatus()) ? "aprobado" : "rechazado";
        String message = String.format(
                "Tu pago #%d ha sido %s. %s",
                payment.getPaymentNumber(),
                status,
                payment.getReviewComment() != null ? payment.getReviewComment() : ""
        );

        String notifType = "APROBADO".equals(payment.getStatus()) ?
                "PAYMENT_APPROVED" : "PAYMENT_REJECTED";

        createNotification(user, message, notifType, "PAYMENT", payment.getPaymentId());

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Pago " + status.toUpperCase(),
                    buildPaymentReviewedEmail(user, payment)
            );
        } catch (MessagingException e) {
            log.error("Error enviando email de revisión de pago", e);
        }
    }

    /**
     * Notifica al cliente 1 día antes del vencimiento
     */
    public void notifyPaymentDue(User user, PaymentSchedule schedule) {
        String message = String.format(
                "Recordatorio: Tu pago #%d vence mañana (%s). Monto: Q%.2f",
                schedule.getPaymentNumber(),
                schedule.getDueDate().toString(),
                schedule.getAmountDue()
        );

        createNotification(user, message, "PAYMENT_REMINDER", "PAYMENT_SCHEDULE", schedule.getScheduleId());

        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Recordatorio de Pago",
                    buildPaymentReminderEmail(user, schedule)
            );
        } catch (MessagingException e) {
            log.error("Error enviando email de recordatorio de pago", e);
        }
    }

    /**
     * Notifica al cobrador sobre pagos vencidos
     */
    public void notifyCollectorOverdue(PaymentSchedule schedule) {
        // Buscar usuarios con rol COLLECTOR
        List<User> collectors = userRepository.findByRole_RoleName("COLLECTOR");

        if (collectors.isEmpty()) {
            log.warn("No se encontraron cobradores para notificar");
            return;
        }

        User client = schedule.getLoan().getLoanApplication().getUser();
        String message = String.format(
                "COBRO: Cliente %s %s tiene pago vencido. Pago #%d. Monto: Q%.2f. Teléfono: %s",
                client.getFirstName(),
                client.getFirstLastName(),
                schedule.getPaymentNumber(),
                schedule.getAmountDue(),
                client.getTelephone()
        );

        for (User collector : collectors) {
            createNotification(collector, message, "OVERDUE_PAYMENT", "PAYMENT_SCHEDULE", schedule.getScheduleId());

            try {
                emailService.sendEmail(
                        collector.getEmail(),
                        "Pago Vencido - Acción Requerida",
                        buildCollectorNotificationEmail(collector, client, schedule)
                );
            } catch (MessagingException e) {
                log.error("Error enviando email al cobrador", e);
            }
        }
    }

    private void createNotification(User user, String message, String type,
                                    String entityType, Long entityId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setSentDate(LocalDateTime.now());
        notification.setReadStatus(false);
        notification.setType(type);
        notification.setRelatedEntityType(entityType);
        notification.setRelatedEntityId(entityId);

        notificationRepository.save(notification);
        log.info("Notificación creada para usuario {}: {}", user.getUserID(), message);
    }

    private String buildPaymentReportedEmail(User user, Payment payment) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2>Pago Reportado</h2>
                <p>Hola <b>%s</b>,</p>
                <p>Hemos recibido tu reporte de pago con los siguientes detalles:</p>
                <ul>
                    <li><b>Número de Pago:</b> %d</li>
                    <li><b>Monto:</b> Q%.2f</li>
                    <li><b>Método:</b> %s</li>
                    <li><b>Fecha:</b> %s</li>
                </ul>
                <p>Tu pago está siendo revisado por nuestro equipo. Te notificaremos cuando sea aprobado.</p>
                <p>Gracias por tu confianza.</p>
            </body>
            </html>
            """,
                user.getFirstName(),
                payment.getPaymentNumber(),
                payment.getAmountPaid(),
                payment.getPaymentMethod(),
                payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    private String buildPaymentReviewedEmail(User user, Payment payment) {
        boolean approved = "APROBADO".equals(payment.getStatus());
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: %s;">Pago %s</h2>
                <p>Hola <b>%s</b>,</p>
                <p>Tu pago #%d ha sido <b>%s</b>.</p>
                %s
                <p>%s</p>
            </body>
            </html>
            """,
                approved ? "#28a745" : "#dc3545",
                approved ? "Aprobado" : "Rechazado",
                user.getFirstName(),
                payment.getPaymentNumber(),
                approved ? "aprobado" : "rechazado",
                payment.getReviewComment() != null ?
                        "<p><b>Comentario:</b> " + payment.getReviewComment() + "</p>" : "",
                approved ? "Tu estado de cuenta ha sido actualizado." :
                        "Por favor, verifica la información y vuelve a reportar el pago."
        );
    }

    private String buildPaymentReminderEmail(User user, PaymentSchedule schedule) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #ffc107;">Recordatorio de Pago</h2>
                <p>Hola <b>%s</b>,</p>
                <p>Te recordamos que tu pago #%d vence <b>mañana</b> (%s).</p>
                <p><b>Monto a pagar:</b> Q%.2f</p>
                <p>Por favor, realiza tu pago a tiempo para evitar cargos adicionales.</p>
                <p>Puedes reportar tu pago desde nuestra plataforma.</p>
            </body>
            </html>
            """,
                user.getFirstName(),
                schedule.getPaymentNumber(),
                schedule.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                schedule.getAmountDue()
        );
    }

    private String buildCollectorNotificationEmail(User collector, User client, PaymentSchedule schedule) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #dc3545;">Pago Vencido - Acción Requerida</h2>
                <p>Hola <b>%s</b>,</p>
                <p>Se ha detectado un pago vencido que requiere gestión de cobro:</p>
                <h3>Información del Cliente:</h3>
                <ul>
                    <li><b>Nombre:</b> %s %s</li>
                    <li><b>Email:</b> %s</li>
                    <li><b>Teléfono:</b> %s</li>
                    <li><b>Dirección:</b> %s</li>
                </ul>
                <h3>Información del Pago:</h3>
                <ul>
                    <li><b>Número de Pago:</b> %d</li>
                    <li><b>Fecha de Vencimiento:</b> %s</li>
                    <li><b>Monto:</b> Q%.2f</li>
                </ul>
                <p>Por favor, procede con la gestión de cobro correspondiente.</p>
            </body>
            </html>
            """,
                collector.getFirstName(),
                client.getFirstName(),
                client.getFirstLastName(),
                client.getEmail(),
                client.getTelephone(),
                client.getAddress() != null ? client.getAddress() : "No especificada",
                schedule.getPaymentNumber(),
                schedule.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                schedule.getAmountDue()
        );
    }
}
package com.solutec.loan_application_server.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
class NotificationDTO {
    private Long notificationId;
    private String message;
    private LocalDateTime sentDate;
    private Boolean readStatus;
    private String type;
}
package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserIDOrderBySentDateDesc(Long userId);
    List<Notification> findByUser_UserIDAndReadStatusOrderBySentDateDesc(Long userId, Boolean readStatus);
}
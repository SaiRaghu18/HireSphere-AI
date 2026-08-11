package com.hirsphere.backend.service;

import com.hirsphere.backend.dto.NotificationDTO;
import com.hirsphere.backend.entity.User;

import java.util.List;

public interface NotificationService {
    NotificationDTO createNotification(User recipient, String title, String message, String type);
    List<NotificationDTO> getUserNotifications(Long recipientId);
    long getUnreadCount(Long recipientId);
    NotificationDTO markAsRead(Long notificationId, Long recipientId);
    void markAllAsRead(Long recipientId);
}

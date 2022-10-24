package ru.b19513.pet_manager.service;

import ru.b19513.pet_manager.controller.entity.NotificationDTO;
import ru.b19513.pet_manager.controller.entity.NotificationScheduleDTO;
import ru.b19513.pet_manager.controller.entity.NotificationTimeoutDTO;
import ru.b19513.pet_manager.controller.entity.StatusDTO;
import ru.b19513.pet_manager.repository.entity.User;

import java.time.LocalTime;
import java.util.List;

public interface NotificationService {
    NotificationTimeoutDTO createNotificationTimeout(long groupId, long petId, String comment, long elapsed);

    NotificationScheduleDTO createNotificationSchedule(long groupId, long petId, String comment, List<LocalTime> times);

    NotificationScheduleDTO updateNotificationSchedule(NotificationScheduleDTO notif);

    NotificationTimeoutDTO updateNotificationTimeout(NotificationTimeoutDTO notif);

    List<NotificationDTO> showNotification(User user);

    StatusDTO deleteNotification( long notifId);

    StatusDTO setTimeInNotificationNote(User user, List<Long> notificationsId);
}

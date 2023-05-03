package ru.b19513.pet_manager.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.b19513.pet_manager.controller.entity.NotificationDTO;
import ru.b19513.pet_manager.controller.entity.NotificationScheduleDTO;
import ru.b19513.pet_manager.controller.entity.NotificationTimeoutDTO;
import ru.b19513.pet_manager.controller.entity.StatusDTO;
import ru.b19513.pet_manager.repository.entity.User;
import ru.b19513.pet_manager.service.NotificationService;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/shelter/notifications")
@Tag(name = "Notifications controller", description = "Контроллер уведомлений")

public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Добавить новое напоминание типа Timeout.")
    @PostMapping("/timeout/")
    public ResponseEntity<StatusDTO> postNotificationTimeout(@RequestParam long groupId, @RequestParam String comment,
                                                          @RequestParam long petId, @RequestParam int elapsed) {
        return ResponseEntity.ok(notificationService.createNotificationTimeout(groupId, petId, comment, elapsed));
    }

    @Operation(summary = "Добавить новое напоминание типа Schedule.")
    @PostMapping("/schedule/")
    public ResponseEntity<StatusDTO> postNotificationSchedule(@RequestParam long groupId, @RequestParam String comment,
                                                                            @RequestParam long petId, @RequestParam String time) {
        return ResponseEntity.ok(notificationService.createNotificationSchedule(groupId, petId, comment, LocalTime.parse(time)));
    }

    @Operation(summary = "Изменить напоминание.")
    @PutMapping("/update")
    public ResponseEntity<NotificationDTO> updateNotification(@RequestBody NotificationDTO notif){
        NotificationDTO notificationDTO;
        if (notif instanceof NotificationTimeoutDTO){
            notificationDTO = notificationService.updateNotificationTimeout((NotificationTimeoutDTO) notif);
        } else {
            notificationDTO = notificationService.updateNotificationSchedule((NotificationScheduleDTO) notif);
        }
        return ResponseEntity.ok(notificationDTO);
    }

    @Operation(summary = "Показать напоминания.")
    @GetMapping("/show")
    public ResponseEntity<List<NotificationDTO>> showNotification(Authentication auth){
        var user = ((User)auth.getPrincipal());
        var notificationList = notificationService.showNotification(user);
        return ResponseEntity.ok(notificationList);
    }

    @Operation(summary = "Удалить напоминание по id.")
    @DeleteMapping("/{notifId}")
    public ResponseEntity<StatusDTO> deleteNotification(@PathVariable long notifId) {
        var status = notificationService.deleteNotification(notifId);
        return ResponseEntity.ok(status);
    }
}

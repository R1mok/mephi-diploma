package ru.b19513.pet_manager.controller.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.b19513.pet_manager.repository.entity.Notification;
import ru.b19513.pet_manager.repository.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Класс, отвечающий за то, когда конкретный пользователь получает уведомление")
public class NotificationNoteDTO {
    @Schema(description = "Пользователь, которому присылается уведомление")
    private User user;
    @Schema(description = "Уведомление")
    private Notification notification;
    @Schema(description = "Время, в которое пользователь получил уведомление последний раз")
    private LocalDateTime lastTime;
}

package ru.b19513.pet_manager.controller.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Schema(description = "Уведомление типа Timeout")
public class NotificationTimeoutDTO extends NotificationDTO{

    @Builder
    public NotificationTimeoutDTO(long id, long groupId, boolean enabled, String comment, String groupName, String petName,
                                  long elapsed, Instant alarmTime) {
        super(id, groupId, enabled, comment, groupName, petName, alarmTime);
        this.elapsed = elapsed;
    }

    @Schema(description = "Время, через которое нужно послать уведомление (в секундах)")
    private long elapsed;
    @Schema(description = "Время отсчета уведомления")
    private LocalDateTime time;

}

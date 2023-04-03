package ru.b19513.pet_manager.controller.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Schema(description = "Уведомление типа Schedule")
public class NotificationScheduleDTO extends NotificationDTO {

    @Builder
    public NotificationScheduleDTO(long id, long groupId, boolean enabled, String comment, String groupName, String petName,
            List<LocalTime> times, Instant alarmTime) {
        super(id, groupId, enabled, comment, groupName, petName, alarmTime);
        this.times = times;
    }

    @Schema(description = "Массив времен, когда нужно нужно посылать уведомление")
    private List<LocalTime> times;
}

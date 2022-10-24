package ru.b19513.pet_manager.controller.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Запись о кормлении")
public class FeedNoteDTO implements Serializable {
    @Schema(description = "Питомец")
    private PetDTO pet;
    @Schema(description = "Пользователь, который оставил запись")
    private UserDTO user;
    @Schema(description = "Время публикации записи")
    private LocalDateTime dateTime;
    @Schema(description = "Дополнительная информация от пользователя")
    private String comment;
}

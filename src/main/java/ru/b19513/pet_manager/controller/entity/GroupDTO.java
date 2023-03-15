package ru.b19513.pet_manager.controller.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.List;

@Data
@Jacksonized
@Builder
@Schema(description = "Группа")
public class GroupDTO implements Serializable {
    @Schema(description = "ID группы")
    private long id;
    @Schema(description = "Название группы")
    private String name;
    @Schema(description = "Описание группы")
    private String description;
    @Schema(description = "Список пользователей группы")
    private List<UserDTO> users;
    @Schema(description = "Список питомцев группы")
    private List<PetDTO> pets;
    @Schema(description = "Количество выгулов в сутки")
    private int walkingCount;
}

package ru.b19513.pet_manager.controller.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
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
}

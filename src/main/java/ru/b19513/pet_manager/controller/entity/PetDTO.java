package ru.b19513.pet_manager.controller.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.b19513.pet_manager.controller.entity.enums.Gender;
import ru.b19513.pet_manager.controller.entity.enums.PetType;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Schema(description = "Питомец")
public class PetDTO implements Serializable {
    @Schema(description = "ID питомца")
    private long id;
    @Schema(description = "Имя питомца")
    private String name;
    @Schema(description = "Описание питомца")
    private String description;
    @Schema(description = "Вид питомца")
    private PetType type;
    @Schema(description = "Пол питомца")
    private Gender gender;
    @Schema(description = "Список замеров роста и веса питомца")
    private List<PetParametersDTO> parametersDTOS;
}

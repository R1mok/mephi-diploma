package ru.b19513.pet_manager.controller.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@Schema(description = "Рост и вес животного")
public class PetParametersDTO {
    @Schema(description = "Рост животного")
    private double height;
    @Schema(description = "Вес животного")
    private double weight;
    @Schema(description = "Время замера")
    private Instant time;
}

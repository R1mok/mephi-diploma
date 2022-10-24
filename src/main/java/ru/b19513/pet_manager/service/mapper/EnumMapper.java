package ru.b19513.pet_manager.service.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnumMapper {
    ru.b19513.pet_manager.controller.entity.enums.Gender entityToDTO(ru.b19513.pet_manager.repository.entity.enums.Gender gender);

    ru.b19513.pet_manager.repository.entity.enums.Gender DTOtoEntity(ru.b19513.pet_manager.controller.entity.enums.Gender gender);


    ru.b19513.pet_manager.controller.entity.enums.PetType entityToDTO(ru.b19513.pet_manager.repository.entity.enums.PetType petType);

    ru.b19513.pet_manager.repository.entity.enums.PetType DTOtoEntity(ru.b19513.pet_manager.controller.entity.enums.PetType petType);
}

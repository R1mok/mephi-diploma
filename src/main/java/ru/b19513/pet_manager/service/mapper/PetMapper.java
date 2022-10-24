package ru.b19513.pet_manager.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.b19513.pet_manager.controller.entity.PetDTO;
import ru.b19513.pet_manager.repository.entity.Pet;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PetMapper {
    PetDTO entityToDTO(Pet entity);

    List<PetDTO> entityToDTO(Collection<Pet> entities);

    void updateEntity(@MappingTarget Pet entity, PetDTO src);
}

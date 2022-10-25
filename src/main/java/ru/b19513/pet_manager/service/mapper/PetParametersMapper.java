package ru.b19513.pet_manager.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.b19513.pet_manager.controller.entity.PetParametersDTO;
import ru.b19513.pet_manager.repository.entity.PetParameters;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PetParametersMapper {
    PetParametersDTO entityToDTO(PetParameters entity);

    List<PetParametersDTO> entityToDTO(Collection<PetParameters> entities);

    void updateEntity(@MappingTarget PetParameters entity, PetParametersDTO src);
}

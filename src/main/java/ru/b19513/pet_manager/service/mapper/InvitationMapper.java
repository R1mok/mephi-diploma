package ru.b19513.pet_manager.service.mapper;

import org.mapstruct.Mapper;
import ru.b19513.pet_manager.controller.entity.InvitationDTO;
import ru.b19513.pet_manager.repository.entity.Invitation;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    GroupMapper groupMapper = new GroupMapperImpl();
    default InvitationDTO entityToDTO(Invitation entity) {
        return InvitationDTO.builder()
                .userId(entity.getUser().getId())
                .group(groupMapper.entityToDTO(entity.getGroup()))
                .build();
    }

    List<InvitationDTO> entityToDTO(Collection<Invitation> entities);
}

package ru.b19513.pet_manager.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.b19513.pet_manager.controller.entity.NotificationDTO;
import ru.b19513.pet_manager.controller.entity.NotificationScheduleDTO;
import ru.b19513.pet_manager.controller.entity.NotificationTimeoutDTO;
import ru.b19513.pet_manager.repository.entity.Notification;
import ru.b19513.pet_manager.repository.entity.NotificationSchedule;
import ru.b19513.pet_manager.repository.entity.NotificationTimeout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mapper
public interface NotificationMapper {
    @Mapping(target = "times",
            expression = "java(entity.getTimes().stream().map(t -> t.getNotifTime()).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "petName", expression = "java(entity.getPet().getName())")
    @Mapping(target = "groupName", expression = "java(entity.getGroup().getName())")
    NotificationScheduleDTO entityToDTO(NotificationSchedule entity);

    @Mapping(target = "times", expression = "java(entity.getTimes())")
    void updateEntity(@MappingTarget NotificationSchedule entity, NotificationScheduleDTO src);

    @Mapping(target = "petName", expression = "java(entity.getPet().getName())")
    @Mapping(target = "groupName", expression = "java(entity.getGroup().getName())")
    NotificationTimeoutDTO entityToDTO(NotificationTimeout entity);

    default List<NotificationDTO> entityToDTO(Collection<Notification> entities) {
        List<NotificationDTO> retValue = new ArrayList<>();
        for (var elem : entities) {
            if (elem instanceof NotificationSchedule) {
                var res = entityToDTO((NotificationSchedule) elem);
                retValue.add(res);
            } else if (elem instanceof NotificationTimeout) {
                var res = entityToDTO((NotificationTimeout) elem);
                retValue.add(res);
            }
        }
        return retValue;
    }

    void updateEntity(@MappingTarget NotificationTimeout entity, NotificationTimeoutDTO src);

}

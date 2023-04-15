package ru.b19513.pet_manager.service.mapper;

import org.mapstruct.Mapper;
import ru.b19513.pet_manager.controller.entity.FeedNoteDTO;
import ru.b19513.pet_manager.repository.entity.Note;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FeedNoteMapper {
    FeedNoteDTO entityToDTO(Note entity);

    List<FeedNoteDTO> entityToDTO(Collection<Note> entities);
}

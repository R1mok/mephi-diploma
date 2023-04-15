package ru.b19513.pet_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.b19513.pet_manager.repository.entity.Note;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedNoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByPetIdAndDateTimeIsAfterAndDateTimeBefore(long petId, LocalDateTime dateTime, LocalDateTime dateTime2);
    List<Note> findByPetId(long petId);
    Note findFirstByPetIdOrderByDateTimeDesc(long petId);
}

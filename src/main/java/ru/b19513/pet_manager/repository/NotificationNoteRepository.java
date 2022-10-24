package ru.b19513.pet_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.b19513.pet_manager.repository.entity.NotificationNote;
import ru.b19513.pet_manager.repository.entity.User;

import java.util.Optional;

@Repository
public interface NotificationNoteRepository extends JpaRepository<NotificationNote, Long> {
    Optional<NotificationNote> findByNotificationIdAndUser(long id, User user);
}

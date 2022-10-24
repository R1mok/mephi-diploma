package ru.b19513.pet_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.b19513.pet_manager.repository.entity.Invitation;

import java.util.Collection;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Invitation.Key> {
    Collection<Invitation> findAllByUserId(Long userId);
}

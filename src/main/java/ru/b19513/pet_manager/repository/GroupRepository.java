package ru.b19513.pet_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.b19513.pet_manager.repository.entity.Group;
import ru.b19513.pet_manager.repository.entity.Invitation;
import ru.b19513.pet_manager.repository.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

}

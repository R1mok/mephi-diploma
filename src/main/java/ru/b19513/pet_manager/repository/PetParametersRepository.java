package ru.b19513.pet_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.b19513.pet_manager.repository.entity.PetParameters;

@Repository
public interface PetParametersRepository extends JpaRepository<PetParameters, Long> {
}

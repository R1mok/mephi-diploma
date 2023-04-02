package ru.b19513.pet_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.b19513.pet_manager.repository.entity.UserDevices;

public interface UserDevicesRepository extends JpaRepository<UserDevices, String> {
}

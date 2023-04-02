package ru.b19513.pet_manager.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.b19513.pet_manager.controller.entity.NotificationScheduleDTO;
import ru.b19513.pet_manager.controller.entity.NotificationTimeoutDTO;
import ru.b19513.pet_manager.controller.entity.enums.Gender;
import ru.b19513.pet_manager.controller.entity.enums.PetType;
import ru.b19513.pet_manager.repository.*;
import ru.b19513.pet_manager.repository.entity.NotificationSchedule;
import ru.b19513.pet_manager.repository.entity.NotificationTimeout;
import ru.b19513.pet_manager.service.GroupService;
import ru.b19513.pet_manager.service.NotificationService;
import ru.b19513.pet_manager.service.PetService;
import ru.b19513.pet_manager.service.UserService;
import ru.b19513.pet_manager.service.mapper.NotificationMapper;

import javax.transaction.Transactional;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class NotificationServiceImplTest {
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    NotificationNoteRepository notificationNoteRepository;
    @Autowired
    PetRepository petRepository;
    @Autowired
    UserService userService;
    @Autowired
    PetService petService;
    @Autowired
    GroupService groupService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    NotificationMapper notificationMapper;

    @Test
    @Transactional
    void createNotificationTimeout() {

    }

    @Test
    @Transactional
    void createNotificationSchedule() {

    }

    @Test
    @Transactional
    void updateNotificationSchedule() {

    }

    @Test
    @Transactional
    void updateNotificationTimeout() {

    }

    @Test
    @Transactional
    void showNotification() throws InterruptedException {

    }

    @Test
    @Transactional
    void deleteNotification() {

    }

    @Test
    @Transactional
    void setTimeInNotificationNote() {

    }
}
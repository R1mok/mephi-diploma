package ru.b19513.pet_manager.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ru.b19513.pet_manager.controller.entity.NotificationDTO;
import ru.b19513.pet_manager.controller.entity.NotificationScheduleDTO;
import ru.b19513.pet_manager.controller.entity.NotificationTimeoutDTO;
import ru.b19513.pet_manager.controller.entity.StatusDTO;
import ru.b19513.pet_manager.exceptions.NotFoundException;
import ru.b19513.pet_manager.exceptions.WrongNotificationClassException;
import ru.b19513.pet_manager.repository.*;
import ru.b19513.pet_manager.repository.entity.*;
import ru.b19513.pet_manager.service.NotificationService;
import ru.b19513.pet_manager.service.fcm.PushNotificationRequest;
import ru.b19513.pet_manager.service.fcm.PushNotificationService;
import ru.b19513.pet_manager.service.mapper.NotificationMapper;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static ru.b19513.pet_manager.consts.Consts.NOTIFICATION_DELETED;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final GroupRepository groupRepository;
    private final PetRepository petRepository;
    private final FeedNoteRepository feedNoteRepository;

    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final PushNotificationService pushNotificationService;
    private final UserDevicesRepository userDevicesRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository, GroupRepository groupRepository, PetRepository petRepository, FeedNoteRepository feedNoteRepository, UserRepository userRepository, NotificationMapper notificationMapper, ThreadPoolTaskScheduler threadPoolTaskScheduler, PushNotificationService pushNotificationService, UserDevicesRepository userDevicesRepository) {
        this.notificationRepository = notificationRepository;
        this.groupRepository = groupRepository;
        this.petRepository = petRepository;
        this.feedNoteRepository = feedNoteRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.pushNotificationService = pushNotificationService;
        this.userDevicesRepository = userDevicesRepository;
    }

    @Override
    public StatusDTO createNotificationTimeout(long groupId, long petId, String comment, long elapsed) {
        var group = groupRepository.findById(groupId)
                .orElseThrow(new NotFoundException("Group with group id " + groupId + " not found"));
        var pet = petRepository.findById(petId)
                .orElseThrow(new NotFoundException("Pet with pet id " + petId + " not found"));
        group.getUsers().forEach(user -> user.getUserDevices()
                .forEach(userDevice -> threadPoolTaskScheduler.schedule(
                        () -> sendNotification(group.getName(), pet.getName(), userDevice.getUserCode(), comment),
                        Instant.now().plus(elapsed, ChronoUnit.SECONDS))));
        var notificationTimeout = NotificationTimeout.builder()
                .elapsed(elapsed)
                .group(group)
                .pet(pet)
                .comment(comment)
                .enabled(true)
                .build();
        notificationTimeout.setAlarmTime(
                Instant.parse(
                        ZonedDateTime.now(ZoneOffset.ofHours(3))
                                .plusSeconds(elapsed)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                )
        );
        notificationTimeout.setTime(LocalDateTime.now());
        var notifSet = pet.getNotifications(); // добавляю к питомцу созданное уведомление
        if (notifSet == null) {
            notifSet = new HashSet<>();
        }
        notifSet.add(notificationTimeout);
        pet.setNotifications(notifSet);
        if (group.getNotificationList() == null) {
            group.setNotificationList(new ArrayList<>());
        }
        var notifInRepo = notificationRepository.save(notificationTimeout);
        group.getNotificationList().addAll(notifSet);
        return StatusDTO.builder().status(HttpStatus.OK).description("Timeout notification added").build();
    }

    @Override
    public StatusDTO createNotificationSchedule(long groupId, long petId, String comment, LocalTime time) {
        var group = groupRepository.findById(groupId)
                .orElseThrow(new NotFoundException("Group with group id " + groupId + " not found"));
        var pet = petRepository.findById(petId)
                .orElseThrow(new NotFoundException("Pet with pet id " + petId + " not found"));
        Date dateTime = ZonedDateTime.now().isAfter(ZonedDateTime.of(time.atDate(LocalDate.now()), ZoneId.of("UTC+3")))
                ? Date.from(time.atDate(LocalDate.now().plusDays(1)).toInstant(ZoneOffset.ofHours(3)))
                : Date.from(time.atDate(LocalDate.now()).toInstant(ZoneOffset.ofHours(3)));
        group.getUsers().forEach(user -> user.getUserDevices()
                .forEach(userDevice -> threadPoolTaskScheduler.scheduleWithFixedDelay(
                        () -> sendNotification(group.getName(), pet.getName(), userDevice.getUserCode(), comment),
                        dateTime, 60000 * 60 * 24)));
        var notificationSchedule = NotificationSchedule.builder()
                .group(group)
                .comment(comment)
                .enabled(false)
                .pet(pet)
                .build();
        notificationSchedule.setAlarmTime(time.atDate(LocalDate.now()).toInstant(ZoneOffset.ofHours(0)));
        var notifSet = pet.getNotifications(); // добавляю к питомцу созданное уведомление
        if (notifSet == null) {
            notifSet = new HashSet<>();
        }
        notifSet.add(notificationSchedule);
        pet.setNotifications(notifSet);
        notificationRepository.save(notificationSchedule);
        return StatusDTO.builder().status(HttpStatus.OK).description("Scheduled notification added").build();
    }

    private void sendNotification(String groupName, String petName, String userCode, String comment) {
        PushNotificationRequest request = new PushNotificationRequest(
                groupName + ": " + petName,
                comment,
                "topic");
        request.setToken(userCode);
        pushNotificationService.sendPushNotificationToToken(request);

    }

    @Override
    public NotificationScheduleDTO updateNotificationSchedule(NotificationScheduleDTO notif) {
        var notificationSchedule = notificationRepository.findById(notif.getId())
                .orElseThrow(new NotFoundException("Notification with notification id " + notif.getId() + " not found"));
        if (notificationSchedule instanceof NotificationSchedule) {
            notificationMapper.updateEntity((NotificationSchedule) notificationSchedule, notif);
            return notificationMapper.entityToDTO((NotificationSchedule) notificationRepository.save(notificationSchedule));
        } else
            throw new WrongNotificationClassException("Found a notification of a different type with notification id " + notificationSchedule.getId());
    }

    @Override
    public NotificationTimeoutDTO updateNotificationTimeout(NotificationTimeoutDTO notif) {
        var notificationTimeout = notificationRepository.findById(notif.getId())
                .orElseThrow(new NotFoundException("Notification with notification id " + notif.getId() + " not found"));
        if (notificationTimeout instanceof NotificationTimeout) {
            notificationMapper.updateEntity((NotificationTimeout) notificationTimeout, notif);
            return notificationMapper.entityToDTO((NotificationTimeout) notificationRepository.save(notificationTimeout));
        } else
            throw new WrongNotificationClassException("Found a notification of a different type with notification id " + notificationTimeout.getId());
    }

    @Override
    public List<NotificationDTO> showNotification(User userFromQuery) {
        var user = userRepository.findById(userFromQuery.getId())
                .orElseThrow(new NotFoundException("user with id " + userFromQuery.getId() + " not found"));
        Set<Group> groupSet;
        if (user.getGroups() == null) {
            groupSet = new HashSet<>();
        } else groupSet = user.getGroups();
        if (user.getOwnedGroups() != null)
            groupSet.addAll(user.getOwnedGroups());
        List<Notification> notificationList = new ArrayList<>();
        groupSet.forEach(g -> notificationList.addAll(
                notificationRepository.findAllByGroupId(g.getId())
        ));
        List<NotificationDTO> resultNotificationList = new ArrayList<>();
        for (var notification : notificationList) {
            if (notification instanceof NotificationTimeout) {
                var notif = (NotificationTimeout) notification;
                if (notif.getAlarmTime().isAfter(
                        Instant.parse(ZonedDateTime.now(ZoneOffset.ofHours(3))
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))))) {
                    resultNotificationList.add(notificationMapper.entityToDTO(notif));
                } else {
                    deleteNotification(notification.getId());
                }
            } else if (notification instanceof NotificationSchedule) {
                var notif = (NotificationSchedule) notification;
                resultNotificationList.add(notificationMapper.entityToDTO(notif));
            }
        }
        return resultNotificationList;
    }

    @Override
    public StatusDTO deleteNotification(long notifId) {
        var notification = notificationRepository.findById(notifId)
                .orElseThrow(new NotFoundException("Notification with notification id " + notifId + " not found"));
        var group = notification.getGroup();
        group.getNotificationList().remove(notification);
        var pet = notification.getPet();
        pet.getNotifications().remove(notification);
        notificationRepository.delete(notification);
        return StatusDTO.builder()
                .status(HttpStatus.OK)
                .description(NOTIFICATION_DELETED)
                .build();
    }
}

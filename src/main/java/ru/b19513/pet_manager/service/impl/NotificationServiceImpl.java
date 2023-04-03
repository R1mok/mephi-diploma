package ru.b19513.pet_manager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.threeten.bp.chrono.ChronoPeriod;
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
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.*;

import static ru.b19513.pet_manager.consts.Consts.NOTIFICATION_DELETED;
import static ru.b19513.pet_manager.consts.Consts.NOTIFICATION_NOTE_UPDATED;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationNoteRepository notificationNoteRepository;
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
    public NotificationServiceImpl(NotificationNoteRepository notificationNoteRepository, NotificationRepository notificationRepository, GroupRepository groupRepository, PetRepository petRepository, FeedNoteRepository feedNoteRepository, UserRepository userRepository, NotificationMapper notificationMapper, ThreadPoolTaskScheduler threadPoolTaskScheduler, PushNotificationService pushNotificationService, UserDevicesRepository userDevicesRepository) {

        this.notificationNoteRepository = notificationNoteRepository;
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
        group.getUsers().forEach(user -> user.getUserDevices()
                .forEach(userDevice -> threadPoolTaskScheduler.scheduleWithFixedDelay(
                        () -> sendNotification(group.getName(), pet.getName(), userDevice.getUserCode(), comment),
                        Date.from(time.atDate(LocalDate.now().plusDays(1)).toInstant(ZoneOffset.ofHours(5))), 60000*60*24)));
        // если LocalTime до текущего времени - завтрашний день, если после, то как сейчас
        var scheduleTimeList = new ArrayList<ScheduleTime>();
        scheduleTimeList.add(ScheduleTime.builder().notifTime(time).build());
        var notificationSchedule = NotificationSchedule.builder()
                .times(scheduleTimeList)
                .group(group)
                .comment(comment)
                .enabled(true)
                .pet(pet)
                .build();
        notificationSchedule.setAlarmTime(time.atDate(LocalDate.now()).toInstant(ZoneOffset.ofHours(5)));
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
                var pet = notif.getPet();
                //var fn = feedNoteRepository.findFirstByPetIdOrderByDateTimeDesc(pet.getId());
                //if (fn != null) {
                //LocalDateTime alarmTime = fn.getDateTime().plusSeconds((notif.getElapsed()));
                var alarmTime = notif.getTime().plusSeconds(notif.getElapsed());
                notif.setAlarmTime(alarmTime.toInstant(ZoneOffset.ofHours(5)));
                boolean notTimeToSend = false;
                //if (notif.getTimes() != null)
                for (var period : notif.getTimes()) {
                    if (alarmTime.isAfter(ChronoLocalDateTime.from(period.getTimeFrom())) &&
                            alarmTime.isBefore(ChronoLocalDateTime.from(period.getTimeTo()))) {
                        notTimeToSend = true;
                    }
                }
                resultNotificationList.add(notificationMapper.entityToDTO(notif));
                //}
            } else if (notification instanceof NotificationSchedule) {
                var notif = (NotificationSchedule) notification;
                var times = notif.getTimes();
                var notificationNote = notificationNoteRepository.findByNotificationIdAndUser(notif.getId(), user);
                LocalDateTime lastTime;
                if (notificationNote.isEmpty()) {
                    lastTime = LocalDateTime.MIN;
                } else lastTime = notificationNote.get().getLastTime();
                for (var time : times) {
                    if (time.getNotifTime().isBefore(LocalTime.now()) && time.getNotifTime().isAfter(LocalTime.from(lastTime))) {
                        resultNotificationList.add(notificationMapper.entityToDTO(notif));
                    }
                }
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

    @Override
    public StatusDTO setTimeInNotificationNote(User user, List<Long> notificationsId) {
        for (var notifId : notificationsId) {
            var notifNote = notificationNoteRepository.findByNotificationIdAndUser(notifId, user)
                    .orElse(NotificationNote.builder()
                            .key(new NotificationNote.Key(user.getId(), notifId))
                            .user(user)
                            .notification(notificationRepository.findById(notifId)
                                    .orElseThrow(new NotFoundException("Notification with notification id " + notifId + " not found")))
                            .build());
            notifNote.setLastTime(LocalDateTime.now());
            notificationNoteRepository.save(notifNote);
        }
        return StatusDTO.builder()
                .status(HttpStatus.OK)
                .description(NOTIFICATION_NOTE_UPDATED)
                .build();
    }
}

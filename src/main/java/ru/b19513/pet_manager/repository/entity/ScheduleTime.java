package ru.b19513.pet_manager.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_schedule_time")
public class ScheduleTime {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column
    private LocalTime notifTime;

    @ManyToOne
    private NotificationSchedule notification;
}

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
@Table(name = "t_period")
public class Period {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column
    private LocalTime timeFrom;

    @Column
    private LocalTime timeTo;

    @ManyToOne(fetch = FetchType.LAZY)
    private NotificationSchedule notification;
}

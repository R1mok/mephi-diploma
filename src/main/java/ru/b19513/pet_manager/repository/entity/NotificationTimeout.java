package ru.b19513.pet_manager.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_notification_timeout")
public class NotificationTimeout extends Notification {

    @Builder
    public NotificationTimeout(Group group, Pet pet, String comment, long elapsed, boolean enabled){
        super(group, pet, comment, enabled);
        this.elapsed = elapsed;

    }
    @Column
    private long elapsed;

    @Column
    private LocalDateTime time;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "notification")
    private List<Period> times;
}
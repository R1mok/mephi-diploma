package ru.b19513.pet_manager.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "t_notification")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Notification {
    public Notification(Group group, Pet pet, String comment, boolean enabled) {
        this.enabled = enabled;
        this.pet = pet;
        this.group = group;
        this.comment = comment;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column
    private boolean enabled;

    @Column
    private String comment;

    @JoinColumn
    @ManyToOne
    private Group group;

    @ManyToOne
    @JoinColumn
    private Pet pet;

    @Column
    private Instant alarmTime;
}

package ru.b19513.pet_manager.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_FEED_NOTE")
public class Note {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Pet pet;

    @Column
    private LocalDateTime dateTime;

    @Column
    private String comment;
}

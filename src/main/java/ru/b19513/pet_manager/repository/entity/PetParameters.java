package ru.b19513.pet_manager.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pet_parameters")
public class PetParameters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn
    private Pet pet;

    @Column
    private double height;

    @Column
    private double weight;

    @Column
    private Instant time;
}

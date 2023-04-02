package ru.b19513.pet_manager.repository.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_USER_DEVICES")
public class UserDevices {
    @Id
    private String userCode;
    @ManyToOne
    private User userId;
}

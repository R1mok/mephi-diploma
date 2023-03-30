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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private User userId;
    @Column
    private String userCode;

    @Override
    public int hashCode() {
        return userCode.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return userCode.equals(obj);
    }
}

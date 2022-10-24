package ru.b19513.pet_manager.controller.entity;

import lombok.Data;

@Data
public class AuthenticationRequestDTO {
    private String login;
    private String password;
}

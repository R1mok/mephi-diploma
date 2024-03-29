package ru.b19513.pet_manager.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.b19513.pet_manager.controller.entity.GroupDTO;
import ru.b19513.pet_manager.controller.entity.InvitationDTO;
import ru.b19513.pet_manager.controller.entity.StatusDTO;
import ru.b19513.pet_manager.controller.entity.UserDTO;
import ru.b19513.pet_manager.repository.entity.User;
import ru.b19513.pet_manager.repository.entity.UserDevices;
import ru.b19513.pet_manager.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/shelter/user")
@Tag(name = "Users controller", description = "Контроллер пользователей")
public class    UsersController {

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestParam String login, @RequestParam String pass,
                                            @RequestParam String name) {
        UserDTO userDTO = userService.signInNewUser(login, pass, name);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Изменение данных пользователя")
    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(Authentication auth, @RequestBody UserDTO user) {
        UserDTO userDTO = userService.updateUser((User) auth.getPrincipal(), user);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Получить список активных приглашений пользователя")
    @GetMapping("/invitations")
    public ResponseEntity<Collection<InvitationDTO>> getInvitations(Authentication auth) {
        Collection<InvitationDTO> invitationDTOCollection = userService.getInvitation((User) auth.getPrincipal());
        return ResponseEntity.ok(invitationDTOCollection);
    }

    @Operation(summary = "Принять приглашение")
    @PutMapping("/accept/{groupId}")
    public ResponseEntity<GroupDTO> acceptInvitation(Authentication auth, @PathVariable long groupId) {
        GroupDTO groupDTO = userService.acceptInvitation(((User) auth.getPrincipal()).getId(), groupId);
        return ResponseEntity.ok(groupDTO);
    }

    @Operation(summary = "Проверить свободность логина")
    @GetMapping("/checkLogin/{login}")
    public ResponseEntity<StatusDTO> isLoginFree(@PathVariable String login) {
        StatusDTO loginFree = userService.isLoginFree(login);
        return ResponseEntity.ok(loginFree);
    }

    @Operation(summary = "Получение данных пользователя")
    @GetMapping("/info")
    public ResponseEntity<UserDTO> getUser(Authentication auth) {
        UserDTO userDTO = userService.getUser((User) auth.getPrincipal());
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Получение пяти пользователей с логином, начинающимся на переданное слово")
    @GetMapping("/findByLogin")
    public ResponseEntity<List<UserDTO>> getUsersByLogin(@RequestParam String loginBegining) {
        List<UserDTO> userDTO = userService.findUsersByLogin(loginBegining);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Получить id пользователя по логину")
    @GetMapping("/getUserByLogin/{login}")
    public ResponseEntity<Long> getUserIdByLogin(@PathVariable String login) {
        return ResponseEntity.ok(userService.getUserIdByLogin(login));
    }

    @Operation(summary = "Добавить токен устройства")
    @PostMapping("/add_user_code")
    public ResponseEntity<StatusDTO> addUserCode(Authentication auth, @RequestParam("user_code") String userCode) {
        Long userId = ((User) auth.getPrincipal()).getId();
        return ResponseEntity.ok(userService.addUserCode(userId, userCode));
    }

    @Operation(summary = "Получить токен устройства для пользователя")
    @GetMapping("/get_user_code")
    public ResponseEntity<Set<String>> getUserCode(Authentication auth) {
        return ResponseEntity.ok(userService.getUserCode(((User) auth.getPrincipal()).getId()));
    }
}

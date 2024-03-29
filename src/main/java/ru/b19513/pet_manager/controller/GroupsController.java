package ru.b19513.pet_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.b19513.pet_manager.controller.entity.GroupDTO;
import ru.b19513.pet_manager.controller.entity.StatusDTO;
import ru.b19513.pet_manager.controller.entity.UserDTO;
import ru.b19513.pet_manager.repository.entity.Group;
import ru.b19513.pet_manager.repository.entity.User;
import ru.b19513.pet_manager.service.GroupService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/shelter/groups")
@Tag(name = "Groups controller", description = "Контроллер групп")
public class GroupsController {

    private final GroupService groupService;

    @Autowired
    public GroupsController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(summary = "Создание новой группы")
    @PostMapping("/create")
    public ResponseEntity<GroupDTO> create(Authentication auth, @RequestParam String name) {
        GroupDTO groupDTO = groupService.createGroup((User) auth.getPrincipal(), name);
        return ResponseEntity.ok(groupDTO);
    }

    @Operation(summary = "Обновление данных группы")
    @PutMapping("/update")
    public ResponseEntity<GroupDTO> update(Authentication auth, @RequestBody GroupDTO group) {
        GroupDTO groupDTO = groupService.updateGroup((User) auth.getPrincipal(), group);
        return ResponseEntity.ok(groupDTO);
    }

    @Operation(summary = "Обновление количества прогулок в группе")
    @PutMapping("/walking")
    public ResponseEntity<StatusDTO> updateWalkingCount(@RequestBody List<GroupDTO> groupDtoList) {
        StatusDTO statusDTO = groupService.updateWalkingCount(groupDtoList);
        return ResponseEntity.ok(statusDTO);
    }

    @Operation(summary = "Приглашение в группу")
    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<StatusDTO> invite(Authentication auth, @PathVariable long groupId, @PathVariable long userId) {
        StatusDTO statusDTO = groupService.inviteUser((User) auth.getPrincipal(), groupId, userId);
        return ResponseEntity.ok(statusDTO);
    }
    
    @Operation(summary = "Получить все группы пользователя")
    @GetMapping("/")
    public ResponseEntity<List<GroupDTO>> getGroups(Authentication auth) {
        List<GroupDTO> groups = groupService.getGroups(((User) auth.getPrincipal()).getId());
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Исключение из группы")
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<GroupDTO> leave(Authentication auth, @PathVariable long groupId, @PathVariable long userId) {
        GroupDTO groupDTO = groupService.kickUser((User) auth.getPrincipal(), groupId, userId);
        return ResponseEntity.ok(groupDTO);
    }

    @Operation(summary = "Удаление группы и всех связанных с ней записей")
    @DeleteMapping("/")
    public ResponseEntity<StatusDTO> delete(Authentication auth, @RequestParam long groupId) {
        StatusDTO statusDTO = groupService.deleteGroup(groupId, (User) auth.getPrincipal());
        return ResponseEntity.ok(statusDTO);
    }

    @Operation(summary = "Получение всех пользователей в группе")
    @GetMapping("/membersList/{groupId}")
    public ResponseEntity<Collection<UserDTO>> getMembersList(@PathVariable long groupId) {
        Collection<UserDTO> users = groupService.getMembersList(groupId);
        return ResponseEntity.ok(users);
    }
}

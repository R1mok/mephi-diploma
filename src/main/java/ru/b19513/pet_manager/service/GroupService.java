package ru.b19513.pet_manager.service;

import ru.b19513.pet_manager.controller.entity.GroupDTO;
import ru.b19513.pet_manager.controller.entity.StatusDTO;
import ru.b19513.pet_manager.repository.entity.User;

public interface GroupService {

    GroupDTO createGroup(User owner, String name);

    GroupDTO updateGroup(User owner, GroupDTO group);

    StatusDTO inviteUser(User owner,long groupId, long userId);

    GroupDTO kickUser(User owner, long groupId, long userId);

    StatusDTO deleteGroup(long groupId, User owner);
}

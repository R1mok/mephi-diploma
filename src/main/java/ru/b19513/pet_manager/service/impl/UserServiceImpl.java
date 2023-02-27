package ru.b19513.pet_manager.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.b19513.pet_manager.controller.entity.GroupDTO;
import ru.b19513.pet_manager.controller.entity.InvitationDTO;
import ru.b19513.pet_manager.controller.entity.StatusDTO;
import ru.b19513.pet_manager.controller.entity.UserDTO;
import ru.b19513.pet_manager.exceptions.LoginBusyException;
import ru.b19513.pet_manager.exceptions.NotFoundException;
import ru.b19513.pet_manager.repository.InvitationRepository;
import ru.b19513.pet_manager.repository.UserRepository;
import ru.b19513.pet_manager.repository.entity.*;
import ru.b19513.pet_manager.repository.*;
import ru.b19513.pet_manager.service.UserService;
import ru.b19513.pet_manager.service.mapper.EnumMapper;
import ru.b19513.pet_manager.service.mapper.GroupMapper;
import ru.b19513.pet_manager.service.mapper.InvitationMapper;
import ru.b19513.pet_manager.service.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

    private final GroupRepository groupRepository;
    private final UserMapper userMapper;
    private final InvitationMapper invitationMapper;
    private final GroupMapper groupMapper;
    private final EnumMapper enumMapper;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(GroupRepository groupRepository, UserMapper userMapper, InvitationMapper invitationMapper, GroupMapper groupMapper,
                           EnumMapper enumMapper, UserRepository userRepository, InvitationRepository invitationRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.groupRepository = groupRepository;
        this.userMapper = userMapper;
        this.invitationMapper = invitationMapper;
        this.groupMapper = groupMapper;
        this.enumMapper = enumMapper;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDTO signInNewUser(String login, String pass, String name) {
        if (userRepository.existsByLogin(login)) {
            throw new LoginBusyException("Login \"" + login + "\" already exist");
        }
        var user = User.builder()
                .login(login)
                .name(name)
                .password(bCryptPasswordEncoder.encode(pass))
                .build();
        return userMapper.entityToDTO(userRepository.save(user));

    }

    @Override
    public UserDTO updateUser(User user, UserDTO userDTO) {
        if (userDTO.getAbout() != null) {
            user.setAbout(userDTO.getAbout());
        }
        if (userDTO.getGender() != null) {
            user.setGender(enumMapper.DTOtoEntity(userDTO.getGender()));
        }
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        return userMapper.entityToDTO(userRepository.save(user));
    }

    @Override
    public Collection<InvitationDTO> getInvitation(User user) {
        var invList = invitationRepository.findAllByUserId(user.getId());
        return invitationMapper.entityToDTO(invList);
    }

    @Override
    public GroupDTO acceptInvitation(long userId, long groupId) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        var invitation = invitationRepository.findById(new Invitation.Key(user.getId(), groupId))
                .orElseThrow(new NotFoundException("Invitation with user id: " + user.getId() + " and group id: " + groupId + "not found"));
        var group = groupRepository.findById(groupId).orElseThrow(new NotFoundException("group with id " + groupId + " not found"));
        if (user.getGroups() == null){
            user.setGroups(new HashSet<>());
        }
        user.getGroups().add(group);
        if (group.getUsers() == null){
            group.setUsers(new HashSet<>());
        }
        group.getUsers().add(user);
        groupRepository.save(group);
        userRepository.save(user);
        invitationRepository.delete(invitation);
        return groupMapper.entityToDTO(group);
    }

    @Override
    public StatusDTO isLoginFree(String login) {
        return StatusDTO.builder()
                .status(HttpStatus.OK)
                .description(userRepository.existsByLogin(login) ? "false" : "true")
                .build();
    }

    @Override
    public List<UserDTO> findUsersByLogin(String login) {
        return userMapper.entityToDTOConf(userRepository.findTop5ByLoginIsStartingWith(login));
    }

    @Override
    public Long getUserIdByLogin(String login) {
        if (userRepository.findByLogin(login).isPresent()) {
            return userRepository.findByLogin(login).get().getId();
        } else {
            throw new NotFoundException("User with login " + login + " not found");
        }
    }

    @Override
    public UserDTO getUser(User user) {
        return userMapper.entityToDTO(user);
    }
}

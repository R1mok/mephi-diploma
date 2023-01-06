package ru.b19513.pet_manager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.b19513.pet_manager.controller.entity.FeedNoteDTO;
import ru.b19513.pet_manager.controller.entity.PetDTO;
import ru.b19513.pet_manager.controller.entity.PetParametersDTO;
import ru.b19513.pet_manager.controller.entity.StatusDTO;
import ru.b19513.pet_manager.controller.entity.enums.Gender;
import ru.b19513.pet_manager.controller.entity.enums.PetType;
import ru.b19513.pet_manager.exceptions.NameTakenException;
import ru.b19513.pet_manager.exceptions.NotFoundException;
import ru.b19513.pet_manager.repository.FeedNoteRepository;
import ru.b19513.pet_manager.repository.GroupRepository;
import ru.b19513.pet_manager.repository.PetRepository;
import ru.b19513.pet_manager.repository.UserRepository;
import ru.b19513.pet_manager.repository.entity.FeedNote;
import ru.b19513.pet_manager.repository.entity.Pet;
import ru.b19513.pet_manager.repository.entity.PetParameters;
import ru.b19513.pet_manager.service.PetService;
import ru.b19513.pet_manager.service.mapper.EnumMapper;
import ru.b19513.pet_manager.service.mapper.FeedNoteMapper;
import ru.b19513.pet_manager.service.mapper.PetMapper;
import ru.b19513.pet_manager.service.mapper.PetParametersMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.b19513.pet_manager.consts.Consts.PET_DELETED;

@Service
public class PetServiceImpl implements PetService {
    private final PetMapper petMapper;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final EnumMapper enumMapper;
    private final FeedNoteMapper feedNoteMapper;
    private final FeedNoteRepository feedNoteRepository;
    private final PetParametersMapper petParametersMapper;

    @Autowired
    public PetServiceImpl(PetMapper petMapper, PetRepository petRepository, UserRepository userRepository, GroupRepository groupRepository, EnumMapper enumMapper, FeedNoteMapper feedNoteMapper, FeedNoteRepository feedNoteRepository, PetParametersMapper petParametersMapper) {
        this.petMapper = petMapper;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.enumMapper = enumMapper;
        this.feedNoteMapper = feedNoteMapper;
        this.feedNoteRepository = feedNoteRepository;
        this.petParametersMapper = petParametersMapper;
    }

    @Override
    public PetDTO createPet(long groupId, String name, String description, Gender gender, PetType petType, Date bornDate) {
        var group = groupRepository.findById(groupId).orElseThrow(new NotFoundException("Group with group id: " + groupId + " not found"));
        if (group.getPets() != null &&
                group.getPets().stream().anyMatch(p -> p.getName().equals(name))) {
            throw new NameTakenException("Pet with name " + name + " already exist");
        }
        var pet = Pet.builder()
                .name(name)
                .group(group)
                .type(enumMapper.DTOtoEntity(petType))
                .gender(enumMapper.DTOtoEntity(gender))
                .bornDate(bornDate)
                .description(description)
                .build();
        if (group.getPets() == null) {
            group.setPets(new HashSet<>());
        }
        group.getPets().add(pet);
        return petMapper.entityToDTO(petRepository.save(pet));
    }


    @Override
    public PetDTO getPet(long petId) {
        var pet = petRepository
                .findById(petId)
                .orElseThrow(new NotFoundException("Pet with pet id " + petId + " not found"));
        return petMapper.entityToDTO(pet);
    }

    @Override
    public PetDTO updatePet(PetDTO petDTO) {
        var pet = petRepository
                .findById(petDTO.getId())
                .orElseThrow(new NotFoundException("Pet with pet id " + petDTO.getId() + " not found"));
        petMapper.updateEntity(pet, petDTO);
        return petMapper.entityToDTO(petRepository.save(pet));
    }

    @Override
    public Collection<PetDTO> getPets(long groupId) {
        var group = groupRepository.findById(groupId).orElseThrow(new NotFoundException("Group wih group id " + groupId + " not found"));
        return petMapper.entityToDTO(group.getPets().stream().sorted((a, b) -> (int) (a.getId() - b.getId())).collect(Collectors.toList()));
    }

    @Override
    public StatusDTO deletePet(long petId) {
        var pet = petRepository.findById(petId).orElseThrow(new NotFoundException("Pet with pet id " + petId + " not found"));
        groupRepository.findAll().stream()
                .filter(e -> e.getPets().contains(pet))
                .forEach(e -> e.getPets().remove(pet));
        petRepository.delete(pet);
        return StatusDTO.builder()
                .status(HttpStatus.OK)
                .description(PET_DELETED)
                .build();
    }

    @Override
    public FeedNoteDTO createFeedNote(long petId, long userId, String comment) {
        var pet = petRepository.findById(petId).orElseThrow(new NotFoundException("Pet with pet id " + petId + " not found"));
        var user = userRepository.findById(userId).orElseThrow(new NotFoundException("User with user id " + userId + " not found"));
        var newFeedNote = FeedNote.builder()
                .pet(pet)
                .user(user)
                .dateTime(LocalDateTime.now())
                .comment(comment)
                .build();
        feedNoteRepository.save(newFeedNote);
        return feedNoteMapper.entityToDTO(newFeedNote);

    }

    @Override
    public Collection<FeedNoteDTO> getFeedNotes(long petId) {
        if (!petRepository.existsById(petId))
            throw new NotFoundException("Pet with pet id " + petId + " not found");
        var collectionOfFeedNotes = feedNoteRepository.findByPetId(petId);
        return feedNoteMapper.entityToDTO(collectionOfFeedNotes);
    }

    @Override
    public Collection<FeedNoteDTO> findFeedNotesByDate(long petId, LocalDateTime from, LocalDateTime to) {
        if (!petRepository.existsById(petId))
            throw new NotFoundException("Pet with pet id " + petId + " not found");
        var collectionOfFeedNotes = feedNoteRepository.findByPetIdAndDateTimeIsAfterAndDateTimeBefore(petId, from, to);
        return feedNoteMapper.entityToDTO(collectionOfFeedNotes);
    }

    @Override
    public PetDTO addNewParameter(long petId, Date date, double weight, double height) {
        var pet0 = petRepository.findById(petId);
        if (pet0.isEmpty())
            throw new NotFoundException("Pet with pet id " + petId + " not found");
        else {
            var pet = pet0.get();
            List<PetParameters> petParametersList = pet.getPetParameters();
            if (petParametersList == null) {
                petParametersList = new ArrayList<>();
            }
            petParametersList.add(PetParameters.builder()
                    .pet(pet)
                    .height(height)
                    .weight(weight)
                    .date(date)
                    .build());
            return petMapper.entityToDTO(petRepository.save(pet));
        }
    }
    @Override
    public Collection<PetParametersDTO> getPetParameters(long petId) {
        var pet0 = petRepository.findById(petId);
        if (pet0.isPresent()) {
            return petParametersMapper.entityToDTO(
                    pet0.get().getPetParameters().stream().limit(10).collect(Collectors.toList()));
        } else throw new NotFoundException("Pet with petId " + petId + "not found");
    }
}

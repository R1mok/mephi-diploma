package ru.b19513.pet_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.b19513.pet_manager.controller.entity.FeedNoteDTO;
import ru.b19513.pet_manager.controller.entity.PetDTO;
import ru.b19513.pet_manager.controller.entity.PetParametersDTO;
import ru.b19513.pet_manager.controller.entity.StatusDTO;
import ru.b19513.pet_manager.controller.entity.enums.Gender;
import ru.b19513.pet_manager.controller.entity.enums.PetType;
import ru.b19513.pet_manager.repository.entity.Pet;
import ru.b19513.pet_manager.repository.entity.PetParameters;
import ru.b19513.pet_manager.repository.entity.User;
import ru.b19513.pet_manager.service.PetService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/shelter/pets")
@Tag(name = "Pets controller", description = "Контроллер питомцев")
public class PetsController {

    private final PetService petService;

    @Autowired
    public PetsController(PetService petService) {
        this.petService = petService;
    }

    @Operation(summary = "Добавить нового питомца")
    @PostMapping("/createPet")
    public ResponseEntity<PetDTO> createPet(@RequestParam long groupId, @RequestParam String name,
                                            @RequestParam Gender gender, @RequestParam PetType petType,
                                            @RequestParam String bornDate) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        PetDTO petDTO = petService.createPet(groupId, name, "", gender, petType, df.parse(bornDate));
        return ResponseEntity.ok(petDTO);
    }

    @Operation(summary = "Обновить сведения о питомце")
    @PutMapping("/update")
    public ResponseEntity<PetDTO> updatePet(@RequestBody PetDTO petDTOInput) {
        PetDTO petDTO = petService.updatePet(petDTOInput);
        return ResponseEntity.ok(petDTO);
    }

    @Operation(summary = "Получить список питомцев по id группы")
    @GetMapping("/byGroup/{groupId}")
    public ResponseEntity<Collection<PetDTO>> getPets(@PathVariable long groupId) {
        Collection<PetDTO> PetDTOCollection = petService.getPets(groupId);
        return ResponseEntity.ok(PetDTOCollection);
    }

    @Operation(summary = "Получить питомца по id")
    @GetMapping("/{petId}")
    public ResponseEntity<PetDTO> getPet(@PathVariable long petId) {
        return ResponseEntity.ok(petService.getPet(petId));
    }


    @Operation(summary = "Удалить питомца и все связанные с ним напоминания и записи")
    @DeleteMapping("/{petId}")
    public ResponseEntity<StatusDTO> deletePet(@PathVariable long petId) {
        StatusDTO statusDTO = petService.deletePet(petId);
        return ResponseEntity.ok(statusDTO);
    }

    @Operation(summary = "Создать запись о кормлении")
    @PostMapping("/createFeedNote")
    public ResponseEntity<FeedNoteDTO> createFeedNote (Authentication auth, @RequestParam long petId,
                                                       @RequestParam String comment){
        var userId = ((User) auth.getPrincipal()).getId();
        FeedNoteDTO feedNoteDTO = petService.createFeedNote(petId, userId, comment);
        return ResponseEntity.ok(feedNoteDTO);
    }

    @Operation(summary = "Получить список записей о кормлении")
    @GetMapping("/{petId}/feedNotes")
    public ResponseEntity<Collection<FeedNoteDTO>> getFeedNotes(@PathVariable long petId) {
        Collection<FeedNoteDTO> feedNoteDTOCollection = petService.getFeedNotes(petId);
        return ResponseEntity.ok(feedNoteDTOCollection);
    }

    @Operation(summary = "Найти записи о кормежках по времени и дате")
    @GetMapping("/{petId}/feedNotesBetweenDates")
    public ResponseEntity<Collection<FeedNoteDTO>> findFeedNotesByDate(@PathVariable long petId,
                                                                       @RequestParam LocalDateTime from,
                                                                       @RequestParam LocalDateTime to) {
        Collection<FeedNoteDTO> feedNoteDTOCollection = petService.findFeedNotesByDate(petId, from, to);
        return ResponseEntity.ok(feedNoteDTOCollection);
    }

    @Operation(summary = "Добавить запись о росте и весе")
    @PostMapping("/parameters/add/{petId}")
    public ResponseEntity<PetDTO> addParametersOfPet(@PathVariable long petId,
                                                                     @RequestParam double weight,
                                                                     @RequestParam double height,
                                                     @RequestParam String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return ResponseEntity.ok(petService.addNewParameter(petId, df.parse(date), weight, height));
    }
    
    @Operation(summary = "Получить записи роста и веса")
    @GetMapping("/parameters/get/{petId}")
    public ResponseEntity<Collection<PetParametersDTO>> getPetParameters(@PathVariable long petId) {
        var petParametersList = petService.getPetParameters(petId);
        return ResponseEntity.ok(petParametersList);
    }
}

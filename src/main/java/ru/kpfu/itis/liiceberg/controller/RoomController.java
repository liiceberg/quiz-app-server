package ru.kpfu.itis.liiceberg.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.liiceberg.dto.*;
import ru.kpfu.itis.liiceberg.exception.ApiNotAvailableException;
import ru.kpfu.itis.liiceberg.exception.RoomNotFoundException;
import ru.kpfu.itis.liiceberg.model.Room;
import ru.kpfu.itis.liiceberg.service.GameService;
import ru.kpfu.itis.liiceberg.service.RoomService;
import ru.kpfu.itis.liiceberg.service.ScoreService;
import ru.kpfu.itis.liiceberg.service.UserService;

import java.util.List;

import static ru.kpfu.itis.liiceberg.dto.MessageDto.Code.*;

@RestController
@RequestMapping(path = "api/room", produces = "application/json")
public class RoomController {
    private final RoomService roomService;
    private final ScoreService scoreService;
    private final GameService gameService;
    private final UserService userService;

    public RoomController(RoomService roomService, ScoreService scoreService, GameService gameService, UserService userService) {
        this.roomService = roomService;
        this.scoreService = scoreService;
        this.gameService = gameService;
        this.userService = userService;
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("create")
    public ResponseEntity<CreateRoomResponse> create(@RequestBody CreateRoomDto dto) throws ApiNotAvailableException {
        Room room = roomService.save(dto);
        gameService.save(room);
        return new ResponseEntity<>(new CreateRoomResponse(room.getCode()), HttpStatus.OK);
    }

    @PostMapping("delete")
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void delete() {
        roomService.deleteOutdated();
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("all")
    public ResponseEntity<List<RoomDto>> getAll() {
        return new ResponseEntity<>(roomService.getAll(), HttpStatus.OK);
    }
//    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @MessageMapping("/game/{room}")
    @SendTo("/topic/game/{room}")
    public MessageDto message(@DestinationVariable String room, @Payload MessageDto message) throws RoomNotFoundException {
        System.out.println(message);
        switch (message.getCode()) {
            case JOIN: {
                roomService.changeRemainingCapacity(room, true);
                return MessageDto.builder()
                        .code(JOIN)
                        .message(String.format("%s joined", getSenderName(message.getSender())))
                        .build();
            }
            case READY: {
                return MessageDto.builder()
                        .code(READY)
                        .message(String.format("%s ready", getSenderName(message.getSender())))
                        .wait(roomService.increaseReadyPlayersNumber(room))
                        .build();
            }
            case EXIT: {
                return MessageDto.builder()
                        .code(EXIT)
                        .message(String.format("%s left", getSenderName(message.getSender())))
                        .wait(roomService.changeRemainingCapacity(room, false))
                        .build();
            }
            case SCORE: {
                scoreService.save(
                        message.getScore(),
                        message.getSender(),
                        room
                );
                Integer count = roomService.getRoomCapacity(room)
                        - roomService.getRemainingCapacity(room)
                        - scoreService.getFinishedPlayers(room);
                return MessageDto.builder()
                        .code(SCORE)
                        .message(String.format("%s finished", getSenderName(message.getSender())))
                        .wait(count)
                        .build();
            }
        }
        return null;
    }

    private String getSenderName(Long id) {
        String name = userService.getName(id);
        return name != null ? name : "user";
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("results")
    public ResponseEntity<List<ScoreDto>> getScores(@RequestParam("code") String code) {
        return new ResponseEntity<>(scoreService.getAllByRoom(code), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("game")
    public ResponseEntity<String> getGameContent(@RequestParam("code") String code) throws ApiNotAvailableException, RoomNotFoundException {
        return new ResponseEntity<>(gameService.getByRoom(code), HttpStatus.OK);
    }

}

package ru.kpfu.itis.liiceberg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.liiceberg.dto.*;
import ru.kpfu.itis.liiceberg.exception.ApiNotAvailableException;
import ru.kpfu.itis.liiceberg.exception.BadArgumentsException;
import ru.kpfu.itis.liiceberg.exception.RoomNotFoundException;
import ru.kpfu.itis.liiceberg.model.Player;
import ru.kpfu.itis.liiceberg.model.Room;
import ru.kpfu.itis.liiceberg.service.*;

import java.util.List;

import static ru.kpfu.itis.liiceberg.dto.MessageDto.Code.*;
@Tag(name = "Room")
@RestController
@RequestMapping(path = "api/room", produces = "application/json")
public class RoomController {
    private final RoomService roomService;
    private final ScoreService scoreService;
    private final GameService gameService;
    private final UserService userService;
    private final PlayerService playerService;

    public RoomController(RoomService roomService, ScoreService scoreService, GameService gameService, UserService userService, PlayerService playerService) {
        this.roomService = roomService;
        this.scoreService = scoreService;
        this.gameService = gameService;
        this.userService = userService;
        this.playerService = playerService;
    }
    @Operation(description = "Create new room")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("create")
    public ResponseEntity<CreateRoomResponse> create(@Validated @RequestBody CreateRoomDto dto) throws ApiNotAvailableException {
        Room room = roomService.save(dto);
        gameService.save(room);
        return new ResponseEntity<>(new CreateRoomResponse(room.getCode()), HttpStatus.OK);
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void delete() {
        roomService.deleteOutdated();
    }
    @Operation(description = "Get all existing rooms")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("all")
    public ResponseEntity<List<RoomDto>> getAll() {
        return new ResponseEntity<>(roomService.getAll(), HttpStatus.OK);
    }

    @SneakyThrows
    @MessageMapping("/game/{room}")
    @SendTo("/topic/game/{room}")
    public MessageDto message(@DestinationVariable String room, @Validated @Payload MessageDto message) {
        System.out.println(message);
        switch (message.getCode()) {
            case JOIN: {
                Integer wait = playerService.addPlayer(roomService.get(room), userService.getById(message.getSender()));
                roomService.updateDatetime(room);
                String serverMessage;
                if (wait < 0) {
                    serverMessage = "Room already full";
                } else {
                    serverMessage = String.format("%s joined", getSenderName(message.getSender()));
                }
                return MessageDto.builder()
                        .code(JOIN)
                        .message(serverMessage)
                        .wait(wait)
                        .build();
            }
            case READY: {
                return MessageDto.builder()
                        .code(READY)
                        .message(String.format("%s ready", getSenderName(message.getSender())))
                        .wait(playerService.increaseReadyPlayersNumber(roomService.get(room)))
                        .build();
            }
            case EXIT: {
                playerService.deleteByUserId(message.getSender());
                return MessageDto.builder()
                        .code(EXIT)
                        .message(String.format("%s left", getSenderName(message.getSender())))
                        .wait(playerService.getAlivePlayersCount(room))
                        .build();
            }
            case SCORE: {
                saveUserRoom(message.getSender(), room);
                scoreService.save(
                        message.getScore(),
                        userService.getById(message.getSender()),
                        roomService.get(room)
                );
                Integer count = playerService.getAlivePlayersCount(room)
                        - scoreService.getFinishedPlayers(room);
                return MessageDto.builder()
                        .code(SCORE)
                        .message(String.format("%s finished", getSenderName(message.getSender())))
                        .wait(count)
                        .build();
            }
            case ALIVE: {
                playerService.confirm(message.getSender());
            }
        }
        return null;
    }
    @Operation(description = "Get actual users scores by room code")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("results")
    public ResponseEntity<List<ScoreDto>> getScores(@RequestParam("code") String code) {
        return new ResponseEntity<>(scoreService.getAllByRoom(code), HttpStatus.OK);
    }
    @Operation(description = "Get actual game content by room code")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("game")
    public ResponseEntity<String> getGameContent(@RequestParam("code") String code) throws ApiNotAvailableException, RoomNotFoundException {
        return new ResponseEntity<>(gameService.getByRoom(code), HttpStatus.OK);
    }
    @Operation(description = "Get players name list by room code")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("players")
    public ResponseEntity<List<String>> getUsersByRoom(@RequestParam("code") String code) throws RoomNotFoundException {
        return new ResponseEntity<>(roomService.getPlayers(code), HttpStatus.OK);
    }

    private String getSenderName(Long id) {
        String name = userService.getName(id);
        return name != null ? name : "user";
    }

    private void saveUserRoom(Long userId, String code) throws BadArgumentsException, RoomNotFoundException {
        roomService.updatePlayers(userService.getById(userId), code);
        userService.addRoom(roomService.get(code), userId);
    }

    private void sendExitMessage(String code, Long id) {
        message(code, MessageDto.builder()
                .sender(id)
                .code(EXIT)
                .build()
        );
    }

    @Transactional
    @Scheduled(fixedRate = 10_000)
    public void verifyPlayers() {
        List<Player> deletedPlayers = playerService.deleteIfNotAlive();
        for (Player p : deletedPlayers) {
            sendExitMessage(p.getRoom().getCode(), p.getUser().getId());
        }
    }

}

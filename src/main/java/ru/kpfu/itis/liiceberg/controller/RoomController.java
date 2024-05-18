package ru.kpfu.itis.liiceberg.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.liiceberg.dto.CreateRoomDto;
import ru.kpfu.itis.liiceberg.dto.CreateRoomResponse;
import ru.kpfu.itis.liiceberg.dto.MessageDto;
import ru.kpfu.itis.liiceberg.dto.RoomDto;
import ru.kpfu.itis.liiceberg.exception.ApiNotAvailableException;
import ru.kpfu.itis.liiceberg.exception.RoomNotFoundException;
import ru.kpfu.itis.liiceberg.model.GameContent;
import ru.kpfu.itis.liiceberg.model.Score;
import ru.kpfu.itis.liiceberg.service.GameService;
import ru.kpfu.itis.liiceberg.service.RoomService;
import ru.kpfu.itis.liiceberg.service.ScoreService;

import java.util.List;

import static ru.kpfu.itis.liiceberg.dto.MessageDto.Code.*;

@RestController
@RequestMapping(path = "api/room", produces = "application/json")
public class RoomController {
    private final RoomService roomService;
    private final ScoreService scoreService;
    private final GameService gameService;
    private final String MESSAGE = "message";
    private final String PLAYERS_TO_WAIT = "players_to_wait";

    public RoomController(RoomService roomService, ScoreService scoreService, GameService gameService) {
        this.roomService = roomService;
        this.scoreService = scoreService;
        this.gameService = gameService;
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("create")
    public ResponseEntity<CreateRoomResponse> create(@RequestBody CreateRoomDto dto) {
        String code = roomService.save(dto);
        return new ResponseEntity<>(new CreateRoomResponse(code), HttpStatus.OK);
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
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @MessageMapping("/game/{room}")
    @SendTo("/topic/game/{room}")
    public MessageDto message(@PathVariable("room") String room, MessageDto message) throws RoomNotFoundException {
        switch (message.getCode()) {
            case JOIN: {
                JSONObject content = new JSONObject();
                content.put(PLAYERS_TO_WAIT, roomService.changeRemainingCapacity(room, true));
                content.put(MESSAGE, String.format("%s joined", message.getSender()));
                return MessageDto.builder()
                        .code(JOIN)
                        .content(content)
                        .build();
            }
            case EXIT: {
                JSONObject content = new JSONObject();
                content.put(PLAYERS_TO_WAIT, roomService.changeRemainingCapacity(room, false));
                content.put(MESSAGE, String.format("%s left", message.getSender()));
                return MessageDto.builder()
                        .code(EXIT)
                        .content(content)
                        .build();
            }
            case SCORE: {

                scoreService.save(
                        message.getContent().getInt("score"),
                        message.getContent().getString("username"),
                        room
                );
                JSONObject content = new JSONObject();
                Integer count = roomService.getRoomCapacity(room)
                        - roomService.getRemainingCapacity(room)
                        - scoreService.getFinishedPlayers(room);
                content.put(PLAYERS_TO_WAIT, count);
                content.put(MESSAGE, String.format("%s finished", message.getSender()));
                return MessageDto.builder()
                        .code(SCORE)
                        .content(content)
                        .build();
            }
        }
        return null;
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("results")
    public ResponseEntity<List<Score>> getScores(@RequestParam("code") String code) {
        return new ResponseEntity<>(scoreService.getAllByRoom(code), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("game")
    public ResponseEntity<GameContent> getGameContent(@RequestParam("code") String code) throws ApiNotAvailableException, RoomNotFoundException {
        return new ResponseEntity<>(gameService.getByRoom(code), HttpStatus.OK);
    }

}

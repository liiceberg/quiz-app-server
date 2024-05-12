package ru.kpfu.itis.liiceberg.controller;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.liiceberg.dto.CreateRoomDto;
import ru.kpfu.itis.liiceberg.dto.MessageDto;
import ru.kpfu.itis.liiceberg.exception.RoomNotFoundException;
import ru.kpfu.itis.liiceberg.model.GameContent;
import ru.kpfu.itis.liiceberg.model.Score;
import ru.kpfu.itis.liiceberg.service.GameService;
import ru.kpfu.itis.liiceberg.service.RoomService;
import ru.kpfu.itis.liiceberg.service.ScoreService;

import java.io.IOException;
import java.util.List;

import static ru.kpfu.itis.liiceberg.dto.MessageDto.Code.*;

@RestController
@RequestMapping(path = "/room", produces = "application/json")
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

    @PostMapping("create")
    public void create(@RequestBody CreateRoomDto dto) {
        roomService.save(dto);
    }

    @GetMapping("delete")
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void delete() {
        roomService.deleteOutdated();
    }

    @MessageMapping("/game/{room}")
    @SendTo("/topic/game/{room}")
    public MessageDto message(@PathVariable("room") String room, MessageDto message) {
        try {
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
        } catch (RoomNotFoundException ex) {
            return MessageDto.builder()
                    .code(NOT_FOUND)
                    .build();
        }
        return null;
    }

    @GetMapping("results")
    public ResponseEntity<List<Score>> getScores(@RequestParam("code") String code) {
        return new ResponseEntity<>(scoreService.getAllByRoom(code), HttpStatus.OK);
    }

    @GetMapping("game")
    public ResponseEntity<GameContent> getGameContent(@RequestParam("code") String code) {
        try {
            return new ResponseEntity<>(gameService.getByRoom(code), HttpStatus.OK);
        } catch (RoomNotFoundException | IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

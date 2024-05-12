package ru.kpfu.itis.liiceberg.service;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.liiceberg.exception.RoomNotFoundException;
import ru.kpfu.itis.liiceberg.model.Room;
import ru.kpfu.itis.liiceberg.model.Score;
import ru.kpfu.itis.liiceberg.repository.RoomRepository;
import ru.kpfu.itis.liiceberg.repository.ScoreRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final RoomRepository roomRepository;
    private final Map<String, Integer> finishedPlayers;

    public ScoreService(ScoreRepository scoreRepository, RoomRepository roomRepository) {
        this.scoreRepository = scoreRepository;
        this.roomRepository = roomRepository;
        finishedPlayers = new HashMap<>();
    }

    public void save(Integer value, String username, String code) throws RoomNotFoundException {
        Optional<Room> optionalRoom = roomRepository.findByCode(code);
        if (!optionalRoom.isPresent()) {
            throw new RoomNotFoundException("Room doesn't exist");
        }
        Score score = Score.builder()
                .value(value)
                .username(username)
                .room(optionalRoom.get())
                .build();
        scoreRepository.save(score);
        saveFinishedPlayer(optionalRoom.get());
    }

    private void saveFinishedPlayer(Room room) {
        Integer number = finishedPlayers.getOrDefault(room.getCode(), 0);
        if (number >= room.getCapacity()) {
            finishedPlayers.put(room.getCode(), 0);
        }
        finishedPlayers.put(room.getCode(), number + 1);
    }

    public Integer getFinishedPlayers(String room) {
        return finishedPlayers.get(room);
    }

    public List<Score> getAllByRoom(String code) {
        return scoreRepository.findAllByRoomCode(code);
    }
}

package ru.kpfu.itis.liiceberg.service;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.liiceberg.dto.ScoreDto;
import ru.kpfu.itis.liiceberg.model.Room;
import ru.kpfu.itis.liiceberg.model.Score;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.repository.ScoreRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final Map<String, Integer> finishedPlayers;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
        finishedPlayers = new HashMap<>();
    }

    public void save(Integer value, User user, Room room) {

        Optional<Score> oldScoresOptional = scoreRepository.findByRoomAndUserId(room, user.getId());

        if (oldScoresOptional.isPresent()) {
            Score oldScores = oldScoresOptional.get();
            oldScores.setValue(oldScores.getValue() + value);
            scoreRepository.save(oldScores);
        }
        else {
            Score score = Score.builder()
                    .value(value)
                    .user(user)
                    .room(room)
                    .build();
            scoreRepository.save(score);
        }

        saveFinishedPlayer(room);
    }

    private void saveFinishedPlayer(Room room) {
        Integer number = finishedPlayers.getOrDefault(room.getCode(), 0);
        if (number >= room.getCapacity()) {
            finishedPlayers.put(room.getCode(), 1);
            return;
        }
        finishedPlayers.put(room.getCode(), number + 1);
    }

    public Integer getFinishedPlayers(String room) {
        return finishedPlayers.get(room);
    }

    public List<ScoreDto> getAllByRoom(String code) {
        return scoreRepository.findAllByRoomCode(code)
                .stream()
                .map(s -> new ScoreDto(s.getUser().getName(), s.getValue()))
                .sorted((s1, s2) -> s2.getValue().compareTo(s1.getValue()))
                .collect(Collectors.toList());
    }
}

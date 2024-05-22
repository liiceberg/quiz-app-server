package ru.kpfu.itis.liiceberg.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.liiceberg.dto.ScoreDto;
import ru.kpfu.itis.liiceberg.exception.RoomNotFoundException;
import ru.kpfu.itis.liiceberg.model.Room;
import ru.kpfu.itis.liiceberg.model.Score;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.repository.RoomRepository;
import ru.kpfu.itis.liiceberg.repository.ScoreRepository;
import ru.kpfu.itis.liiceberg.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final Map<String, Integer> finishedPlayers;

    public ScoreService(ScoreRepository scoreRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.scoreRepository = scoreRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        finishedPlayers = new HashMap<>();
    }

    public void save(Integer value, Long id, String code) throws RoomNotFoundException {

        Optional<Score> oldScoresOptional = scoreRepository.findByRoomCodeAndUserId(code, id);

        Optional<Room> optionalRoom = roomRepository.findByCode(code);
        if (!optionalRoom.isPresent()) {
            throw new RoomNotFoundException("Room doesn't exist");
        }

        if (oldScoresOptional.isPresent()) {
            Score oldScores = oldScoresOptional.get();
            oldScores.setValue(oldScores.getValue() + value);
            scoreRepository.save(oldScores);
        }
        else {
            Optional<User> user = userRepository.findById(id);
            if (!user.isPresent()) {
                throw new UsernameNotFoundException(id.toString());
            }
            Score score = Score.builder()
                    .value(value)
                    .user(user.get())
                    .room(optionalRoom.get())
                    .build();
            scoreRepository.save(score);
        }

        saveFinishedPlayer(optionalRoom.get());
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

package ru.kpfu.itis.liiceberg.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.liiceberg.exception.RoomNotFoundException;
import ru.kpfu.itis.liiceberg.model.GameContent;
import ru.kpfu.itis.liiceberg.model.Room;
import ru.kpfu.itis.liiceberg.repository.GameRepository;
import ru.kpfu.itis.liiceberg.repository.RoomRepository;

import java.io.IOException;
import java.util.Optional;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final RoomRepository roomRepository;
    private final TriviaService triviaService;

    public GameService(GameRepository gameRepository, RoomRepository roomRepository, TriviaService triviaService) {
        this.gameRepository = gameRepository;
        this.roomRepository = roomRepository;
        this.triviaService = triviaService;
    }
    @Transactional
    public GameContent getByRoom(String code) throws RoomNotFoundException, IOException {
        gameRepository.deleteIfRequestsGreaterThanCapacity(code);
        Optional<GameContent> optionalContent = gameRepository.findByRoomCode(code);
        GameContent content;
        if (!optionalContent.isPresent()) {
            content = save(code);
        } else {
            content = optionalContent.get();
        }
        return increaseRequestsCount(content);
    }
    public GameContent save(String code) throws RoomNotFoundException, IOException {
        Optional<Room> optionalRoom = roomRepository.findByCode(code);
        if (!optionalRoom.isPresent()) {
            throw new RoomNotFoundException();
        }
        Room room = optionalRoom.get();
        String content = triviaService.getTrivia(10, room.getCategory(), room.getDifficulty(), "multiple");
        System.out.println(content.length());
        GameContent gameContent = GameContent.builder()
                .content(content)
                .requestsCount(0)
                .room(room)
                .build();
        return gameRepository.save(gameContent);
    }
    private GameContent increaseRequestsCount(GameContent gameContent) {
        gameContent.setRequestsCount(gameContent.getRequestsCount() + 1);
        return gameRepository.save(gameContent);
    }
}

package ru.kpfu.itis.liiceberg.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.liiceberg.exception.ApiNotAvailableException;
import ru.kpfu.itis.liiceberg.exception.RoomNotFoundException;
import ru.kpfu.itis.liiceberg.model.GameContent;
import ru.kpfu.itis.liiceberg.model.Room;
import ru.kpfu.itis.liiceberg.repository.GameRepository;

import java.util.Optional;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final TriviaService triviaService;

    public GameService(GameRepository gameRepository, TriviaService triviaService) {
        this.gameRepository = gameRepository;
        this.triviaService = triviaService;
    }

    @Transactional
    public String getByRoom(String code) throws ApiNotAvailableException, RoomNotFoundException {
        Optional<GameContent> optionalContent = gameRepository.findByRoomCode(code);
        if (!optionalContent.isPresent()) {
            throw new RoomNotFoundException("Game content for this room not found");
        }
        GameContent content = optionalContent.get();
        String getContent = content.getContent().toString();
        increaseRequestsCount(content);

        return getContent;
    }

    public void save(Room room) throws ApiNotAvailableException {
        JSONObject content = getContent(room);
        GameContent gameContent = GameContent.builder()
                .content(content)
                .requestsCount(0)
                .room(room)
                .build();
        gameRepository.save(gameContent);
    }

    private JSONObject getContent(Room room) throws ApiNotAvailableException {
        String content = triviaService.getTrivia(10, room.getCategory(), room.getDifficulty(), "multiple");
        return new JSONObject(content);
    }

    private void increaseRequestsCount(GameContent gameContent) throws ApiNotAvailableException {
        Room r = gameContent.getRoom();
        if (gameContent.getRequestsCount() + 1 >= r.getCapacity()) {
            gameContent.setRequestsCount(0);
            gameContent.setContent(getContent(r));
        } else {
            gameContent.setRequestsCount(gameContent.getRequestsCount() + 1);
        }
        gameRepository.save(gameContent);
    }
}

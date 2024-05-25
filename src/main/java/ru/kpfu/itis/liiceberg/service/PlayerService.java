package ru.kpfu.itis.liiceberg.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.liiceberg.model.Player;
import ru.kpfu.itis.liiceberg.model.Room;
import ru.kpfu.itis.liiceberg.model.User;
import ru.kpfu.itis.liiceberg.repository.PlayerRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final Map<Long, Long> confirmationTime;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        confirmationTime = new HashMap<>();
    }

    public void save(User user, Room room) {
        Player player = playerRepository.save(new Player(user, room));
        confirmationTime.put(player.getUser().getId(), System.currentTimeMillis());
    }

    @Transactional
    public List<Player> deleteIfNotAlive() {
        for (Long userId : confirmationTime.keySet()) {
            if (System.currentTimeMillis() - confirmationTime.get(userId) > 15_000) {
                playerRepository.setNotAlive(userId);
                confirmationTime.remove(userId);
            }
        }
        return playerRepository.deleteByIsAliveFalse();
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        playerRepository.deleteByUserId(userId);
    }

    public void confirm(Long id) {
        confirmationTime.put(id, System.currentTimeMillis());
    }

}

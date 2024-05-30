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
    private final Map<String, Integer> unreadyPlayers;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        confirmationTime = new HashMap<>();
        unreadyPlayers = new HashMap<>();
    }

    public Integer addPlayer(Room room, User user) {
        Integer currentPlayersNumber = getAlivePlayersCount(room.getCode());
         if (currentPlayersNumber < room.getCapacity()) {
             save(user, room);
             ++currentPlayersNumber;
             return room.getCapacity() - currentPlayersNumber;
         }
         return -1;
    }

    public Integer increaseReadyPlayersNumber(Room room) {
        if (!unreadyPlayers.containsKey(room.getCode())) {
            unreadyPlayers.put(room.getCode(), room.getCapacity());
        }
        if (unreadyPlayers.get(room.getCode()) <= 0) {
            unreadyPlayers.put(room.getCode(), room.getCapacity() - 1);
        } else {
            unreadyPlayers.put(room.getCode(), unreadyPlayers.get(room.getCode()) - 1);
        }
        return unreadyPlayers.get(room.getCode());
    }

    public Integer getAlivePlayersCount(String code) {
        return playerRepository.getAlivePlayersCountByRoomCode(code);
    }

    private void save(User user, Room room) {
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

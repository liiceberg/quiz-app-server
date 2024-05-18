package ru.kpfu.itis.liiceberg.service;

import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.liiceberg.dto.CreateRoomDto;
import ru.kpfu.itis.liiceberg.dto.RoomDto;
import ru.kpfu.itis.liiceberg.exception.RoomNotFoundException;
import ru.kpfu.itis.liiceberg.model.Room;
import ru.kpfu.itis.liiceberg.repository.RoomRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomRepository repository;
    private final Long ACTIVE_PERIOD = (long) (24 * 60 * 60 * 1000);
    private final Map<String, Integer> roomCapacity;

    public RoomService(RoomRepository repository) {
        this.repository = repository;
        roomCapacity = new HashMap<>();
    }

    public String save(CreateRoomDto dto) {
        Room room = Room.builder()
                .code(RandomString.make(6))
                .capacity(dto.getCapacity())
                .difficulty(dto.getDifficulty())
                .category(dto.getCategory())
                .datetime(System.currentTimeMillis())
                .build();
        repository.save(room);
        return room.getCode();
    }
    @Transactional
    public void deleteOutdated() {
        Long latestDate = System.currentTimeMillis() - ACTIVE_PERIOD;
        repository.deleteRoomByDatetimeIsBefore(latestDate);
    }

    public Integer getRoomCapacity(String code) {
        return repository.getCapacityByCode(code);
    }

    public Integer changeRemainingCapacity(String roomCode, boolean isIncrease) throws RoomNotFoundException {
        if (!roomCapacity.containsKey(roomCode)) {
            roomCapacity.put(roomCode, getRoomCapacity(roomCode));
        }
         int remainingCapacity = roomCapacity.get(roomCode);
        int k = 1;
        if (isIncrease) {
            k = -1;
            if (remainingCapacity + k < 0) {
                throw new RoomNotFoundException("Room already full");
            }
        }
        roomCapacity.put(roomCode, remainingCapacity + k);

        return roomCapacity.get(roomCode);
    }

    public Integer getRemainingCapacity(String room) {
        return roomCapacity.get(room);
    }

    public List<RoomDto> getAll() {
        return repository.findAllOrderByDatetime()
                .stream()
                .map(r -> new RoomDto(r.getCode(), r.getCapacity(), r.getCategory(), r.getDifficulty()))
                .collect(Collectors.toList());
    }


}

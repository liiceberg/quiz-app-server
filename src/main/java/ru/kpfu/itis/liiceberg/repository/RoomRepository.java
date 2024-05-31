package ru.kpfu.itis.liiceberg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.liiceberg.model.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByCode(String code);
    void deleteRoomByDatetimeIsBefore(Long datetime);
    @Query("select r from Room r order by r.datetime desc")
    List<Room> findAllOrderByDatetime();
    @Modifying
    @Query("update Room r set r.datetime = :datetime where r.code = :code")
    void updateDatetime(String code, Long datetime);
}

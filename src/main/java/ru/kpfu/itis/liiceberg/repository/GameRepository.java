package ru.kpfu.itis.liiceberg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.liiceberg.model.GameContent;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<GameContent, Long> {
    Optional<GameContent> findByRoomCode(String code);
    @Modifying
    @Query("delete from GameContent g where g.room = (select r from Room r where r.code = :code)" +
            " and g.requestsCount >= (select r.capacity from Room r where r.code = :code)")
    void deleteIfRequestsGreaterThanCapacity(String code);

}

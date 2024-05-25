package ru.kpfu.itis.liiceberg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.liiceberg.model.Player;

import java.util.List;


@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> deleteByIsAliveFalse();
    void deleteByUserId(Long id);
    @Modifying
    @Query("update Player p set p.isAlive = false where p.user = (select u from User u where u.id = :userId)")
    void setNotAlive(Long userId);

}

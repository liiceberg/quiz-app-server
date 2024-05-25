package ru.kpfu.itis.liiceberg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.liiceberg.model.GameContent;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<GameContent, Long> {
    Optional<GameContent> findByRoomCode(String code);
}

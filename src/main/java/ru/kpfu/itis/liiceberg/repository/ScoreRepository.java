package ru.kpfu.itis.liiceberg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.liiceberg.model.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findAllByRoomCode(String code);

    Optional<Score> findByRoomCodeAndUserId(String code, Long id);
}

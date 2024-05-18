package ru.kpfu.itis.liiceberg.service;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.liiceberg.exception.ApiNotAvailableException;
import ru.kpfu.itis.liiceberg.repository.TriviaRepository;

import java.io.IOException;

@Service
public class TriviaService {
    private final TriviaRepository triviaRepository;

    public TriviaService(TriviaRepository triviaRepository) {
        this.triviaRepository = triviaRepository;
    }

    public String getCategories() throws ApiNotAvailableException {
        try {
            return triviaRepository.getCategories();
        } catch (IOException e) {
            throw new ApiNotAvailableException();
        }
    }

    public String getTrivia(Integer amount, Integer category, String difficulty, String type) throws ApiNotAvailableException {
        try {
            return triviaRepository.getTrivia(amount, category, difficulty, type);
        } catch (IOException e) {
            throw new ApiNotAvailableException();
        }
    }

}

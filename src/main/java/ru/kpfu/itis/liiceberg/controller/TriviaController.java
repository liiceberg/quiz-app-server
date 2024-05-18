package ru.kpfu.itis.liiceberg.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.itis.liiceberg.exception.ApiNotAvailableException;
import ru.kpfu.itis.liiceberg.service.TriviaService;

@RestController
@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
@RequestMapping(path = "api/trivia", produces = "application/json")
public class TriviaController {
    private final TriviaService triviaService;

    public TriviaController(TriviaService triviaService) {
        this.triviaService = triviaService;
    }

    @GetMapping("categories")
    public ResponseEntity<String> getCategories() throws ApiNotAvailableException {
        String response = triviaService.getCategories();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<String> get(@RequestParam("amount") Integer amount,
                                      @RequestParam("category") Integer category,
                                      @RequestParam("difficulty") String difficulty,
                                      @RequestParam("type") String type) throws ApiNotAvailableException{
        String response = triviaService.getTrivia(amount, category, difficulty, type);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

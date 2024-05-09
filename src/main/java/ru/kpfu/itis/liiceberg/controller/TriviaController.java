package ru.kpfu.itis.liiceberg.controller;

import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kpfu.itis.liiceberg.service.TriviaService;

import java.net.URL;

@RestController
@RequestMapping(path = "api/trivia", produces = "application/json")
public class TriviaController {
    private final TriviaService triviaService;

    public TriviaController(TriviaService triviaService) {
        this.triviaService = triviaService;
    }

    @SneakyThrows
    @GetMapping("categories")
    public ResponseEntity<String> getCategories() {
        URL url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("opentdb.com")
                .path("api_category.php")
                .build()
                .toUri()
                .toURL();
        try {
            String response = triviaService.makeGetRequest(url);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<String> get(@RequestParam("amount") Integer amount,
                                      @RequestParam("category") Integer category,
                                      @RequestParam("difficulty") String difficulty,
                                      @RequestParam("type") String type) {
        URL url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("opentdb.com")
                .path("api.php")
                .queryParam("amount", amount)
                .queryParam("category", category)
                .queryParam("difficulty", difficulty)
                .queryParam("type", type)
                .build()
                .toUri()
                .toURL();
        try {
            String response = triviaService.makeGetRequest(url);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

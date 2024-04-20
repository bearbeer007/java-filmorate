package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {

    private int id = 0;

    private final Map<Integer, Film> films = new HashMap<>();

    @SneakyThrows
    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {

        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Date is before 1895-12-28");
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Film was create");
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Film not exist");
        }
        films.put(film.getId(), film);
        log.info("Film was update");
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return new ResponseEntity<>(new ArrayList<>(films.values()), HttpStatus.OK);
    }
}

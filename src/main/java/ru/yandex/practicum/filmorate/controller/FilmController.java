package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmStorage;


import java.util.List;


@Slf4j
@RestController
@RequestMapping(value = "/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmStorage films ;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Film > Post Request {}", film);
        return films.createFilm(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        log.info("Film > Put Request {}", film);
        return films.updateFilm(film);
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Film > Get All Request");
        return films.readAllFilms();
    }
}

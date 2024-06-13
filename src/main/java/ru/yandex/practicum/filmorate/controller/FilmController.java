package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.interfaces.FilmService;
import ru.yandex.practicum.filmorate.services.interfaces.LikesService;

import java.util.Collection;
import java.util.List;


@RestController
@Validated
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final LikesService likesService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Request body: " + film.toString());
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Request body: " + film.toString());
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping(value = "/{id}")
    public Film findFilmById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        likesService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        likesService.deleteLike(id, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive Long count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        log.info(String.format("GET /films/popular?count={count}&genreId={genreId}&year={year}, {count} = %s, " +
                "{genreID} = %s, {year} = %s", count, genreId, year));
        return filmService.getPopularFilms(count, genreId, year);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteFilmById(@PathVariable("id") Long id) {
        filmService.deleteFilm(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonsFilms(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "friendId") Long friendId
    ) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{id}")
    public Collection<Film> getFilmsDirector(@Valid @PathVariable(value = "id") Long id,
                                             @RequestParam(value = "sortBy") String obj) {
        return filmService.getFilmsSortByYearOrLikes(id, obj);
    }

    @GetMapping(value = "/search")
    public List<Film> searchFilms(@Valid @RequestParam String query,
                                  @RequestParam String by) {
        return filmService.searchFilmsByTitleAndDirector(query, by);
    }
}

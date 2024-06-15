package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmSortParameters;
import ru.yandex.practicum.filmorate.services.interfaces.FilmService;
import ru.yandex.practicum.filmorate.services.interfaces.LikesService;

import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@Validated
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final LikesService likesService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление фильма {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Запрос на получение сех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping(value = "/{id}")
    public Film findFilmById(@PathVariable Long id) {
        log.info("Запрос на получение фильма с id {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос на добавление лайка фильму с id {}, пользователем с id {}", id, userId);
        likesService.addLike(id, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Запрос на удаление лайка фильму с id {}, пользователем с id {}", id, userId);
        likesService.deleteLike(id, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive Long count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        log.info("Запрос на получение популярных фильмов");
        return filmService.getPopularFilms(count, genreId, year);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteFilmById(@PathVariable("id") Long id) {
        log.info("Удаление фильма с id {}", id);
        filmService.deleteFilm(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonsFilms(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "friendId") Long friendId
    ) {
        log.info("Получение общих фильмов пользователей с id {} и {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{id}")
    public Collection<Film> getFilmsDirector(@Valid @PathVariable(value = "id") Long id,
                                             @RequestParam(value = "sortBy") FilmSortParameters param) {
        log.info("Получение отсортированных фильмов по правилу {}", param);
        return filmService.getFilmsSortByYearOrLikes(id, param);
    }

    @GetMapping(value = "/search")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam String by) {
        log.info("Запрос на поиск фильма by title and director");
        return filmService.searchFilmsByTitleAndDirector(query, by);
    }
}

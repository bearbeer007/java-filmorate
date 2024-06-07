package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;


public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> findFilmById(Long id);

    List<Film> getPopularFilms(Long count);

    void addGenreToFilm(Long filmId, Integer genreId);

    void addGenresToFilm(Long filmId, List<Integer> genreIds);

    List<Film> getCommonFilms(Long userId, Long friendId);
}

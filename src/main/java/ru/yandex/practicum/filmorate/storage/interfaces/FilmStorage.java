package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Optional<Film> findFilmById(Long id);

    List<Film> getPopularFilms(Long count, Integer genreId, Integer year);

    void addGenreToFilm(Long filmId, Integer genreId);

    void addGenresToFilm(Long filmId, List<Integer> genreIds);

    void deleteFilm(Long id);

    List<Film> getCommonFilms(Long userId, Long friendId);

    boolean contains(Long id);

    List<Long> findSimilarUsersByLikes(Long userId);

    List<Film> findRecommendedFilms(Long userId, Long similarUserId);

    List<Film> searchFilmsByTitleAndDirector(String query, String obj);
}

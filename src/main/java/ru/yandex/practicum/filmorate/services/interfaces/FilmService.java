package ru.yandex.practicum.filmorate.services.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmSortParameters;

import java.util.List;
import java.util.Set;

public interface FilmService {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    List<Film> getPopularFilms(Long count, Integer genreId, Integer year);

    void deleteFilm(Long id);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getRecommendFilms(Long userId);

    List<Film> searchFilmsByTitleAndDirector(String query, String obj);

    List<Film> getFilmsSortByYearOrLikes(Long id, FilmSortParameters param);

}

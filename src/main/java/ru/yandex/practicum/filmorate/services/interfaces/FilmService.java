package ru.yandex.practicum.filmorate.services.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long id);

    List<Film> getPopularFilms(Long count);

    void deleteFilm(Long id);
}

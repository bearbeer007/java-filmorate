package ru.yandex.practicum.filmorate.services.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface GenreService {
    Set<Genre> getAllGenres();

    Genre getGenreById(Integer id);

    Set<Genre> getAllGenresByFilm(Long id);
}

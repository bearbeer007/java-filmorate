package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    Set<Genre> getAllGenres();

    Optional<Genre> findGenreById(Integer id);

    Set<Genre> getAllGenresByFilm(Long id);

    void deleteGenresByFilm(Long id);

    void clearTableGenres(Long id);
}

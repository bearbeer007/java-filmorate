package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {

    Director addDirector(Director director);

    List<Director> getAllDirectors();

    Optional<Director> findDirectorById(Long id);

    Director updateDirector(Director director);

    Optional<Director> removeDirectorById(Long id);

    Set<Director> getDirectorsFilm(Long id);

    void setDirectorToFilm(Film film);

    List<Film> getFilmsSortByYearOrLikes(Long id, String obj);
}

package ru.yandex.practicum.filmorate.services.interfaces;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {

    Director addDirector(Director director);

    List<Director> getAllDirectors();

    Director findDirectorById(Long id);

    Director updateDirector(Director director);

    String removeDirectorById(Long id);
}

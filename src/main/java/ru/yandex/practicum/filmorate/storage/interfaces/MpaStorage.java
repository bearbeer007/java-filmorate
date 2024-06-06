package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    List<Mpa> getAllRatings();

    Optional<Mpa> findRatingById(Integer id);

    Optional<Mpa> findRatingByFilmId(Long id);
}

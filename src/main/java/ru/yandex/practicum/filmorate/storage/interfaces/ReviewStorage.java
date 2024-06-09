package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> get(Long id);

    Long add(Review review);

    void update(Review review);

    List<Review> getByFilmId(Integer filmId, Integer count);

    void deleteById(Long id);

    boolean contains(Long id);
}

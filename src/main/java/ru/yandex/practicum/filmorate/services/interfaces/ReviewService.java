package ru.yandex.practicum.filmorate.services.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Long add(Review review);

    void update(Review review);

    List<Review> getByFilmId(Integer filmId, Integer count);

    void deleteById(Long id);

    Review getById(Long id);

    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void deleteLike(int reviewId, int userId);

    void deleteDislike(int reviewId, int userId);
}

package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.services.interfaces.ReviewService;
import ru.yandex.practicum.filmorate.storage.interfaces.*;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowClass;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    protected final ReviewStorage reviewStorage;
    protected final ReviewLikeStorage reviewLikeStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    @Override
    public Review add(final Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());

        Review reviewAdded = reviewStorage.add(review);

        eventStorage.addEvent(MapRowClass.mapRowToEvent(review.getUserId(), reviewAdded.getId(), EventType.REVIEW, Operation.ADD));

        return reviewAdded;
    }

    @Override
    public void update(final Review review) {
        checkReviewExists(review.getId());
        reviewStorage.update(review);

        Review reviewNew = reviewStorage.get(review.getId()).get();

        eventStorage.addEvent(MapRowClass.mapRowToEvent(reviewNew.getUserId(), reviewNew.getId(), EventType.REVIEW, Operation.UPDATE));
    }

    @Override
    public List<Review> getByFilmId(Integer filmId, Integer count) {

        return reviewStorage.getByFilmId(filmId, count);
    }

    @Override
    public void deleteById(Long id) {
        checkReviewExists(id);

        Review review = reviewStorage.get(id).get();

        reviewStorage.deleteById(id);

        eventStorage.addEvent(MapRowClass.mapRowToEvent(review.getUserId(), review.getId(), EventType.REVIEW, Operation.REMOVE));
    }

    @Override
    public Review getById(Long id) {
        return reviewStorage.get(id).orElseThrow(() -> new NotFoundException("Нет отзыва с id " + id));
    }

    @Override
    public void addLike(int reviewId, int userId) {
        reviewLikeStorage.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        reviewLikeStorage.addDislike(reviewId, userId);
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        reviewLikeStorage.deleteLike(reviewId, userId);
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {
        reviewLikeStorage.deleteDislike(reviewId, userId);
    }

    private void checkReviewExists(Long reviewId) {
        if (!reviewStorage.contains(reviewId)) {
            throw new NotFoundException("Нет отзыва с id " + reviewId);
        }
    }

    private void checkFilmExists(Long filmId) {
        if (!filmStorage.contains(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    private void checkUserExists(Long userId) {
        if (!userStorage.contains(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
    }
}
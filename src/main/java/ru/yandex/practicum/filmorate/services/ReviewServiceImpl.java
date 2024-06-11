package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.services.interfaces.ReviewService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

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

    public Long add(final Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());

        Long reviewId = reviewStorage.add(review);

        eventStorage.addEvent(review.getUserId(), reviewId, EventType.REVIEW, Operation.ADD);

        return reviewId;
    }

    public void update(final Review review) {
        checkReviewExists(review.getId());
        reviewStorage.update(review);

        Review reviewNew = reviewStorage.get(review.getId()).get();

        eventStorage.addEvent(reviewNew.getUserId(), reviewNew.getId(), EventType.REVIEW, Operation.UPDATE);
    }

    public List<Review> getByFilmId(Integer filmId, Integer count) {

        return reviewStorage.getByFilmId(filmId, count);
    }

    public void deleteById(Long id) {
        checkReviewExists(id);

        Review review = reviewStorage.get(id).get();

        reviewStorage.deleteById(id);

        eventStorage.addEvent(review.getUserId(), review.getId(), EventType.REVIEW, Operation.REMOVE);
    }

    public Review getById(Long id) {
        final Optional<Review> reviewOpt = reviewStorage.get(id);

        if (reviewOpt.isEmpty()) {
            throw new NotFoundException("Нет отзыва с id " + id);
        }

        return reviewOpt.get();
    }

    public void addLike(int reviewId, int userId) {
        reviewLikeStorage.addLike(reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        reviewLikeStorage.addDislike(reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        reviewLikeStorage.deleteLike(reviewId, userId);
    }

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
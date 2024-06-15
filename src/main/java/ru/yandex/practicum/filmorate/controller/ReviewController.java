package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.services.interfaces.ReviewService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review add(@Valid @RequestBody Review newReview) {
        log.info("Добавление review {}", newReview);
        return reviewService.add(newReview);
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        log.info("Обновление review {}", review);
        reviewService.update(review);
        return reviewService.getById(review.getId());
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        log.info("Удаление review {}", id);
        reviewService.deleteById(id);
    }


    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable(name = "id") Long id) {
        log.info("Получение review с id {}", id);
        return reviewService.getById(id);
    }


    @GetMapping
    public List<Review> getReviewsByFilm(@RequestParam(name = "filmId") Optional<Integer> filmId, @RequestParam(name = "count", defaultValue = "10") int count) {
        log.info("Получение review для фильма с id {}", filmId);
        return reviewService.getByFilmId(filmId.orElse(null), count);
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info("Добавление лайка review с id {}, пользователем с id {}", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info("Добавление дизлайка review с id {}, пользователем с id {}", id, userId);
        reviewService.addDislike(id, userId);
    }


    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info("Удаление лайка review с id {}, пользователем с id {}", id, userId);
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info("Удаление дизлайка review с id {}, пользователем с id {}", id, userId);
        reviewService.deleteDislike(id, userId);
    }
}
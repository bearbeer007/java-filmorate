package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.services.interfaces.ReviewService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review add(@Valid @RequestBody Review newReview) {
        final Long reviewId = reviewService.add(newReview);
        return reviewService.getById(reviewId);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        reviewService.update(review);
        return reviewService.getById(review.getId());
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        reviewService.deleteById(id);
    }


    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable(name = "id") Long id) {
        return reviewService.getById(id);
    }


    @GetMapping
    public List<Review> getReviewsByFilm(@RequestParam(name = "filmId") Optional<Integer> filmId, @RequestParam(name = "count", defaultValue = "10") int count) {
        return reviewService.getByFilmId(filmId.orElse(null), count);
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        reviewService.addDislike(id, userId);
    }


    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        reviewService.deleteDislike(id, userId);
    }
}
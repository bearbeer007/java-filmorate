package ru.yandex.practicum.filmorate.services.interfaces;

public interface LikesService {

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}

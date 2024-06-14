package ru.yandex.practicum.filmorate.services.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

public interface LikesService {

    Film addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}

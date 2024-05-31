package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;

public interface LikesStorage {
    Set<Long> getLikes(Long id);

    Film addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}

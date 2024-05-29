package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.Set;

public interface LikesStorage {
    Set<Long> getLikes(Long id);
}

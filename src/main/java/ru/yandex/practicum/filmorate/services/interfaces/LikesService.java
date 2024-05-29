package ru.yandex.practicum.filmorate.services.interfaces;

import java.util.Set;

public interface LikesService {
    Set<Long> getLikes(Long id);
}

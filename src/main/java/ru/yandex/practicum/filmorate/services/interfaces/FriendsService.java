package ru.yandex.practicum.filmorate.services.interfaces;

import java.util.Set;

public interface FriendsService {
    Set<Long> getFriends(Long id);
}

package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.Set;

public interface FriendsStorage {
    Set<Long> getFriends(Long id);
}

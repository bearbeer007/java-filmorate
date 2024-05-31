package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendsStorage {
    Set<Long> getFriends(Long id);

    User addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getFriendsList(Long id);

    List<User> commonFriends(Long userFirst, Long userSecond);
}

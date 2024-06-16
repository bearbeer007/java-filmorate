package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {

    User addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getFriendsList(Long id);

    List<User> commonFriends(Long userFirst, Long userSecond);
}

package ru.yandex.practicum.filmorate.services.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    List<User> addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getFriends(Long id);

    List<Optional<User>> commonFriends(Long userFirst, Long userSecond);
}
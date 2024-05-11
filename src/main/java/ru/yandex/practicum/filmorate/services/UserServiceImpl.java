package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User addUser(User user) {
        checkName(user);
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        checkName(user);
        return userStorage.updateUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Long id) {
        return findUserById(id);
    }

    @Override
    public List<User> addFriend(Long userId, Long friendId) {
        validateDifferentIds(userId, friendId);
        User user1 = findUserById(userId);
        User user2 = findUserById(friendId);
        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
        return List.of(user1, user2);
    }

    @Override
    public List<User> deleteFriend(Long userId, Long friendId) {
        validateDifferentIds(userId, friendId);
        User user1 = findUserById(userId);
        User user2 = findUserById(friendId);
        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
        return List.of(user1, user2);
    }

    @Override
    public List<User> getFriends(Long id) {
        User user = findUserById(id);
        return user.getFriends().stream()
                .map(this::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> commonFriends(Long userFirst, Long userSecond) {
        validateDifferentIds(userFirst, userSecond);
        User user1 = findUserById(userFirst);
        User user2 = findUserById(userSecond);
        Set<Long> commonFriendIds = new HashSet<>(user1.getFriends());
        commonFriendIds.retainAll(user2.getFriends());
        return commonFriendIds.stream()
                .map(this::findUserById)
                .collect(Collectors.toList());
    }


    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private User findUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id: %s, отсутствует", userId)));
    }

    private void validateDifferentIds(Long id1, Long id2) {
        if (id1.equals(id2)) {
            throw new ValidationException("Identical IDs. The user cannot add himself as a friend.");
        }
    }
}

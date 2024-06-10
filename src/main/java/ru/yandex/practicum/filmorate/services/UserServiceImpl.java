package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;


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
    public List<Event> getEvents(Long id) {
        checkUserExists(id);
        return eventStorage.getEventsByUserId(id);
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private User findUserById(Long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id: %s, отсутствует", userId)));
    }

    private boolean isUserExists(Long id) {
        return userStorage.contains(id);
    }

    private void checkUserExistsWithException(Long id, final String message) {
        if (!isUserExists(id)) {
            throw new NotFoundException(message);
        }
    }

    private void checkUserExists(Long id) {
        checkUserExistsWithException(id, "Нет пользователя " +id);
    }

    private void checkFriendExists(Long id) {
        checkUserExistsWithException(id, "Нет пользователя " +id);
    }

    private void checkOtherUserExists(Long id) {
        checkUserExistsWithException(id, String.format("Нет пользователя (с кем должны быть общие друзья) с id = %s", id));
    }

}

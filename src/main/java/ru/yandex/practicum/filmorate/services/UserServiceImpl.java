package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;

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
        findUserById(id); // This will throw NotFoundException if the user doesn't exist
        return eventStorage.getEventsByUserId(id);
    }

    @Override
    public void deleteUser(Long id) {
        findUserById(id); // This ensures the user exists before attempting to delete
        userStorage.deleteUser(id);
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
}
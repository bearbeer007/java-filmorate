package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 1;

    @Override
    public User addUser(User user) {
        user.setId(idGenerator);
        log.info("Пользователь добавлен, присвоен номер id: {}.", user.getId());
        users.put(user.getId(), user);
        idGenerator++;
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            log.info("Пользователь с номером id: {}, обновлен.", user.getId());
            users.put(user.getId(), user);
            return user;
        } else {
            log.info("Пользователь с таким id: " + user.getId() + " отсутствует");
            throw new NotFoundException("Пользователь с таким id: " + user.getId() + " отсутствует");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}
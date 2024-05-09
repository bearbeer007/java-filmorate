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

    private final Map<Long, User> users = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public User addUser(User userFromRequest) {
        User user = userFromRequest.toBuilder()
                .name(userFromRequest.getName() == null || userFromRequest.getName().isBlank() ?
                        userFromRequest.getLogin() : userFromRequest.getName())
                .id(idGenerator++)
                .build();
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("Пользователь с таким id: " + user.getId() + " отсутствует");
            throw new NotFoundException(String.format("User with id=%d was not found.", user.getId()));
        }
        log.info("Пользователь с номером id: {}, обновлен.", user.getId());
        if (user.getName().isBlank()) user.setName(user.getLogin());
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (users.containsKey(id)) {
            return Optional.ofNullable(users.get(id));
        } else {
            throw new NotFoundException(String.format("User with id=%d was not found.", id));
        }
    }
}
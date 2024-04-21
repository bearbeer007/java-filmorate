package ru.yandex.practicum.filmorate.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    private int id = 1;

    public User createUser(User user) {
        user.setId(id++);
        return users.put(user.getId(), user);
    }

    public List<User> readAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("User with id " + user.getId() + " not found");
        }
        return users.put(user.getId(), user);
    }
}
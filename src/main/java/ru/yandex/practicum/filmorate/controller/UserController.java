package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final UserStorage users = new UserStorage();

    @SneakyThrows
    @GetMapping
    public List<User> findAll() {
        log.info("User > Get All Request");
        return users.readAllUsers();
    }

    @SneakyThrows
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("User > Post Request {}", user);
        users.createUser(user);
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.info("User > Put Request {}", user);
        users.updateUser(user);
        return user;
    }
}

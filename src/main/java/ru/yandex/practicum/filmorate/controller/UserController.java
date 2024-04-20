package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private static int id = 0;
    public final Map<Integer, User> users = new HashMap<>();

    @SneakyThrows
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.debug("user = " + user);
        log.info("Login of user = " + user.getLogin());
        log.info("Default name of user = " + user.getName());
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("User was create");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Login of user = " + user.getLogin());
        log.info("Default name of user = " + user.getName());
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("User not exist");
        }
        users.put(user.getId(), user);
        log.info("User was update");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(new ArrayList<>(users.values()), HttpStatus.OK);
    }
}

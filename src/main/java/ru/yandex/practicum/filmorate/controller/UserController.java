package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.interfaces.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Request body: " + user.toString());
        log.info("Request body: " + userService.addUser(user).toString());
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Request body: " + user.toString());
        log.info("Request body: " + userService.updateUser(user).toString());
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public User findUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable("id") Long id,
                                @PathVariable("friendId") Long friendId) {
        List<User> friends = userService.addFriend(id, friendId);
        return friends;
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable("id") Long id,
                                   @PathVariable("friendId") Long friendId) {
        List<User> friends = userService.deleteFriend(id, friendId);
        return friends;

    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Long id) {
        return userService.getFriends(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Long id,
                                       @PathVariable("otherId") Long otherId) {
        List<User> commonFriends = userService.commonFriends(id, otherId);
        return commonFriends;
    }
}

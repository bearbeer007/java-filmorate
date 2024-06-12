package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.interfaces.FilmService;
import ru.yandex.practicum.filmorate.services.interfaces.FriendsService;
import ru.yandex.practicum.filmorate.services.interfaces.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final FriendsService friendsService;
    private final FilmService filmService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
          log.info("Request body: " + user.toString());
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Request body: " + user.toString());
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
    public void addFriend(@PathVariable("id") Long id,
                          @PathVariable("friendId") Long friendId) {
        friendsService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long id,
                             @PathVariable("friendId") Long friendId) {
        friendsService.deleteFriend(id, friendId);

    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Long id) {
        return friendsService.getFriendsList(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Long id,
                                       @PathVariable("otherId") Long otherId) {
        List<User> commonFriends = friendsService.commonFriends(id, otherId);
        return commonFriends;
    }

    @GetMapping("/{id}/feed")
    public List<Event> getEventsOfUser(@PathVariable(name = "id") Long userId) {
        log.info(String.format("GET /users/{id}/feed, {id} = %s", userId));
        return userService.getEvents(userId);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendFilms(@PathVariable Long id) {
        return filmService.getRecommendFilms(id);
    }
}

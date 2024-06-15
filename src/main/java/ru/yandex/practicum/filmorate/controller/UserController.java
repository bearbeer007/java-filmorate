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

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FriendsService friendsService;
    private final FilmService filmService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление пользователя {}", user);
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя");
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public User findUserById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя с id {}", id);
        return userService.getUserById(id);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Long id,
                          @PathVariable("friendId") Long friendId) {
        log.info("Запрос на добавление в друзья пользователей с id {} и {}", id, friendId);
        friendsService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long id,
                             @PathVariable("friendId") Long friendId) {
        log.info("Запрос на удаление из друзей пользователей с id {} и {}", id, friendId);
        friendsService.deleteFriend(id, friendId);

    }

    @GetMapping(value = "/{id}/friends")
    public List<User> getFriends(@PathVariable("id") Long id) {
        log.info("Запрос на получение списка всех друзей пользователя с id {}", id);
        return friendsService.getFriendsList(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") Long id,
                                       @PathVariable("otherId") Long otherId) {
        log.info("Запрос на получение списка всех общих друзей пользователей с id {} и {}", id, otherId);
        List<User> commonFriends = friendsService.commonFriends(id, otherId);
        return commonFriends;
    }

    @GetMapping("/{id}/feed")
    public List<Event> getEventsOfUser(@PathVariable(name = "id") Long userId) {
        log.info("Запрос на получение всех event для пользователя с id {}", userId);
        return userService.getEvents(userId);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUserById(@PathVariable("id") Long id) {
        log.info("Запрос на удаление пользователя с id {}", id);
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendFilms(@PathVariable Long id) {
        log.info("Запрос на получение списка рекомендованных фильмов для пользователя с id {}", id);
        return filmService.getRecommendFilms(id);
    }
}

package ru.yandex.practicum.filmorate.response;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

public class UserFilmResponse {

    public User user;
    public Film film;

    public UserFilmResponse(User user, Film film) {
        this.user = user;
        this.film = film;
    }
}

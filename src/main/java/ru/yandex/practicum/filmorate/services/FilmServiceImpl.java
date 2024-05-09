package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.interfaces.FilmService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        return findFilmById(id);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        User user = findUserById(userId);
        Film film = findFilmById(filmId);

        if (!user.getLikedFilms().contains(film.getId())) {
            user.getLikedFilms().add(film.getId());
            film.setLikes(film.getLikes() + 1);
        }
        return film;
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        User user = findUserById(userId);
        Film film = findFilmById(filmId);

        if (user.getLikedFilms().contains(film.getId())) {
            user.getLikedFilms().remove(film.getId());
            film.setLikes(film.getLikes() - 1);
        }
    }

    @Override
    public List<Film> getPopularFilms(Long size) {
        if (size == null) size = 10L;
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingLong(Film::getLikes).reversed()
                        .thenComparingLong(Film::getId))
                .limit(size)
                .collect(Collectors.toList());
    }

    private User findUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Film findFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с таким id: %s, отсутствует", filmId)));
    }
}
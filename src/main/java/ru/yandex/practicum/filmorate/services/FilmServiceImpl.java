package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.interfaces.FilmService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Comparator;
import java.util.List;
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
        return filmStorage.getFilmById(id).orElseThrow(
                () -> new NotFoundException(String.format("Фильм с таким id: %s, отсутствует", id)));
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        var film = getFilmById(filmId);
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException(String.format("Фильм с таким id: %s, отсутствует", userId)));
        if (film.getLikeIds().contains(userId)) {
            throw new NotFoundException("One user - one like, exceeded the allowed number of likes");
        }
        film.getLikeIds().add(userId);
        return film;
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        var film = getFilmById(filmId);
        userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException(String.format("Фильм с таким id: %s, отсутствует", userId)));
        if (!film.getLikeIds().remove(userId)) {
            throw new NotFoundException(
                    String.format("No like from user with id - %s to film with id - %s", userId, filmId));
        }
    }

    @Override
    public List<Film> getPopularFilms(Long size) {
        return filmStorage.getAllFilms().stream()
                .filter(film -> !film.getLikeIds().isEmpty())
                .sorted(Comparator.comparingInt((Film film) -> film.getLikeIds().size()).reversed())
                .limit(size)
                .collect(Collectors.toList());
    }
}

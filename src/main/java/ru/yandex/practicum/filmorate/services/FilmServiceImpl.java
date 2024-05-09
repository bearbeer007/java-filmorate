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
        return filmStorage.getFilmById(id).orElseThrow(
                () -> new NotFoundException(String.format("Фильм с таким id: %s, отсутствует", id)));
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);

        if (optionalUser.isEmpty() || optionalFilm.isEmpty()) {
            throw new NotFoundException("User or film not found");
        }

        User user = optionalUser.get();
        Film film = optionalFilm.get();

        if (!user.getLikedFilms().contains(film.getId())) {
            user.getLikedFilms().add(film.getId());
            film.setLikes(film.getLikes() + 1);
        }
        return film;
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Optional<User> optionalUser = userStorage.getUserById(userId);
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);
        User user = optionalUser.get();
        Film film = optionalFilm.get();

        if (user.getLikedFilms().contains(film.getId())) {
            user.getLikedFilms().remove(film.getId());
            film.setLikes(film.getLikes() - 1);
        }
    }

    @Override
    public List<Film> getPopularFilms(Long size) {
        if (size == null) size = 10l;
        return filmStorage.getAllFilms().stream()
                .sorted(new Comparator<Film>() {
                    @Override
                    public int compare(Film o1, Film o2) {
                        if (o1.getLikes() == o2.getLikes()) return Integer.compare(o1.getId(), o2.getId());
                        return Integer.compare(o2.getLikes(), o1.getLikes());
                    }
                })
                .limit(size)
                .collect(Collectors.toList());
    }
}

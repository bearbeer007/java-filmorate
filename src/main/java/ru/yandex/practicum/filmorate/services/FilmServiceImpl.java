package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.BadRequestException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.services.interfaces.*;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final LikesService likesService;

    @Override
    public Film addFilm(Film film) {
        mpaIds(film);
        Film createdFilm = filmStorage.addFilm(film);
        if (film.getLikeIds() == null) {
            film.setLikeIds(new HashSet<>());
        }
        if (!film.getLikeIds().isEmpty()) {
            for (Long likeId : film.getLikeIds()) {
                filmStorage.addLike(createdFilm.getId(), likeId);
            }
        }
        if (film.getGenres() != null) {
            List<Integer> films = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            filmStorage.addGenresToFilm(film.getId(), films);
        }
        return createdFilm;
    }

    private void mpaIds(Film film) {
        var mpaIds = mpaService.getAllMpaRatings().stream().map(Mpa::getId).collect(Collectors.toList());
        if (!mpaIds.contains(film.getMpa().getId())) {
            throw new BadRequestException("Передан не существующий рейтинг Mpa.");
        }
        if (film.getGenres() != null) {
            var genresIdsDb = genreService.getAllGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            var filmsGenres = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            for (Integer filmsGenreId : filmsGenres) {
                if (!genresIdsDb.contains(filmsGenreId)) {
                    throw new BadRequestException("Передан не существующий жанр");
                }
            }
        }
    }

    @Override
    public Film updateFilm(Film film) {
        mpaIds(film);
        Film updatedFilm = filmStorage.updateFilm(film);

        if (film.getLikeIds() != null) {
            for (Long likeId : film.getLikeIds()) {
                filmStorage.addLike(updatedFilm.getId(), likeId);
            }
        }

        if (film.getGenres() != null) {
            List<Integer> genresIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
            if (!genresIds.isEmpty()) {
                filmStorage.addGenresToFilm(film.getId(), genresIds);
            }
        }
        return updatedFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = filmStorage.findFilmById(id).orElseThrow(
                () -> new NotFoundException(String.format("Фильм с таким id: %s, отсутствует", id)));
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .duration(film.getDuration())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .mpa(mpaService.getRatingByFilmId(id))
                .likeIds(likesService.getLikes(film.getId()))
                .genres(genreService.getAllGenresByFilm(film.getId()))
                .build();
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        var film = getFilmById(filmId);
        userService.getUserById(userId);
        if (likesService.getLikes(filmId).contains(userId)) {
            throw new BadRequestException("One user - one like, exceeded the allowed number of likes");

        }
        return filmStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        var film = getFilmById(filmId);
        userService.getUserById(userId);

        if (!likesService.getLikes(filmId).contains(userId)) {
            throw new NotFoundException(
                    String.format("No like from user with id - %s to film with id - %s", userId, filmId));
        }
        filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Long size) {
        return filmStorage.getPopularFilms(size);
    }

}
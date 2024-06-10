package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.BadRequestException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.services.interfaces.*;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {


    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaService;
    private final GenreDbStorage genreService;
    private final LikesDbStorage likesStorage;


    @Override
    public Film addFilm(Film film) {
        mpaIds(film);
        Film createdFilm = filmDbStorage.addFilm(film);
        if (film.getLikeIds() == null) {
            film.setLikeIds(new HashSet<>());
        }
        if (!film.getLikeIds().isEmpty()) {
            for (Long likeId : film.getLikeIds()) {
                likesStorage.addLike(createdFilm.getId(), likeId);
            }
        }
        if (film.getGenres() != null) {
            List<Integer> films = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            filmDbStorage.addGenresToFilm(film.getId(), films);
        }
        return createdFilm;
    }

    private void mpaIds(Film film) {
        var mpaIds = mpaService.getAllRatings().stream().map(Mpa::getId).collect(Collectors.toList());
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
        Film updatedFilm = filmDbStorage.updateFilm(film);

        if (film.getLikeIds() != null) {
            for (Long likeId : film.getLikeIds()) {
                likesStorage.addLike(updatedFilm.getId(), likeId);
            }
        }

        if (film.getGenres() != null) {
            List<Integer> genresIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
            if (!genresIds.isEmpty()) {
                filmDbStorage.addGenresToFilm(film.getId(), genresIds);
            }
        }
        return updatedFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        return filmDbStorage.getAllFilms();
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = filmDbStorage.findFilmById(id).orElseThrow(
                () -> new NotFoundException(String.format("Фильм с таким id: %s, отсутствует", id))
        );

        Mpa mpa = mpaService.findRatingByFilmId(id).orElseThrow(
                () -> new NotFoundException(String.format("Рейтинг для фильма с id: %s, отсутствует", id))
        );

        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .duration(film.getDuration())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .mpa(mpa)
                .likeIds(likesStorage.getLikes(film.getId()))
                .genres(genreService.getAllGenresByFilm(film.getId()))
                .build();
    }


    @Override
    public List<Film> getPopularFilms(Long size) {
        return filmDbStorage.getPopularFilms(size);
    }

}
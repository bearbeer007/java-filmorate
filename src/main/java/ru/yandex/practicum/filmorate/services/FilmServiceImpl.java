package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.BadRequestException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.FilmSortParameters;
import ru.yandex.practicum.filmorate.services.interfaces.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final LikesDbStorage likesStorage;
    private final UserDbStorage userDbStorage;
    private final DirectorDbStorage directorStorage;

    @Override
    public Film addFilm(Film film) {
        mpaIds(film);
        Film createdFilm = filmDbStorage.addFilm(film);
        if (film.getLikeIds() == null) {
            film.setLikeIds(new HashSet<>());
        }
        if (!film.getLikeIds().isEmpty()) {
            for (Long likeId : film.getLikeIds()) {
                likesStorage.addLike(film.getId(), likeId);
            }
        }
        if (film.getGenres() != null) {
            List<Integer> films = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            filmDbStorage.addGenresToFilm(film.getId(), films);
        }
        if (film.getDirectors() != null) {
            directorStorage.setDirectorToFilm(film);
        }
        return getFilmById(createdFilm.getId());
    }

    private void mpaIds(Film film) {
        var mpaIds = mpaDbStorage.getAllRatings().stream().map(Mpa::getId).toList();
        if (!mpaIds.contains(film.getMpa().getId())) {
            throw new BadRequestException("Передан не существующий рейтинг Mpa.");
        }
        if (film.getGenres() != null) {
            var genresIdsDb = genreDbStorage.getAllGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList();
            var filmsGenres = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList();
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
            List<Integer> genresIds = film.getGenres().stream().map(Genre::getId).toList();
            if (!genresIds.isEmpty()) {
                genreDbStorage.clearTableGenres(film.getId());
                genresIds.forEach(genre -> filmDbStorage.addGenreToFilm(film.getId(), genre));
            } else {
                genreDbStorage.deleteGenresByFilm(film.getId());
            }
        }
        if (film.getDirectors() != null) {
            directorStorage.setDirectorToFilm(film);
        } else {
            directorStorage.removeFilmDirectorById(film.getId());
        }
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getAllFilms() {
        return filmDbStorage.getAll();
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = filmDbStorage.findFilmById(id).orElseThrow(
                () -> new NotFoundException(String.format("Фильм с таким id: %s, отсутствует", id))
        );

        Mpa mpa = mpaDbStorage.findRatingByFilmId(id).orElseThrow(
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
                .genres(genreDbStorage.getAllGenresByFilm(film.getId()))
                .directors(directorStorage.getDirectorsFilm(film.getId()))
                .build();
    }

    @Override
    public List<Film> getPopularFilms(Long size, Integer genreId, Integer year) {
        return filmDbStorage.getPopularByGenresAndYear(size, genreId, year);
    }

    @Override
    public void deleteFilm(Long id) {
        filmDbStorage.deleteFilm(id);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        userDbStorage.findUserById(userId);
        userDbStorage.findUserById(friendId);
        List<Film> films = filmDbStorage.getCommonFilms(userId, friendId);
        return films.stream()
                .map(this::getFullFilmObject)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Film> getRecommendFilms(Long userId) {
        userDbStorage.findUserById(userId);

        List<Long> similarUserIds = filmDbStorage.findSimilarUsersByLikes(userId);

        return filmDbStorage.findRecommendedFilmsBySimilarUsers(userId, similarUserIds).stream()
                .map(this::getFullFilmObject)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Film> getFilmsSortByYearOrLikes(Long id, FilmSortParameters param) {
        if (directorStorage.getDirectorsByIds(List.of(id)).isEmpty()) {
            throw new NotFoundException("Режиссера с данным ID не найдено");
        }
        return getSortedFilmByDirector(param, id);
    }

    public List<Film> getSortedFilmByDirector(FilmSortParameters param, Long directorId) {
        return filmDbStorage.getSortedFilmByDirector(param, directorId);
    }

    @Override
    public List<Film> searchFilmsByTitleAndDirector(String query, String obj) {
        return filmDbStorage.search(query, obj);
    }

    private Film getFullFilmObject(Film film) {
        return film.toBuilder()
                .likeIds(likesStorage.getLikes(film.getId()))
                .mpa(mpaDbStorage.findRatingByFilmId(film.getId()).get())
                .genres(genreDbStorage.getAllGenresByFilm(film.getId()))
                .directors(directorStorage.getDirectorsFilm(film.getId().longValue()))
                .build();
    }
}
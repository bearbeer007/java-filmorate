package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.ListFilmExtractor;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final String sqlGetAll = "select f.id, f.name, f.description, f.release_date, f.duration, " +
            "m.id as mpa_id, m.name as mpa_name, g.id as genre_id, g.name as genre_name, l.USER_ID " +
            "from films f " +
            "left join RATINGS m on f.rating_mpa_id = m.id " +
            "left join film_genres fg on f.id = fg.film_id " +
            "left join genres g on fg.genre_id = g.id " +
            "left join like_films l on f.id = l.film_id " +
            "GROUP BY f.id, g.id, l.USER_ID ORDER BY f.id";

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into films (name, description, release_date, duration, rating_mpa_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_mpa_id = ? " +
                "where id = ?";
        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) == 0) {
            log.info("Film with id: {} not found.", film.getId());
            throw new NotFoundException("Wrong film ID: " + film.getId());
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(sqlGetAll, new ListFilmExtractor());
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        String sqlGetById = "select f.*, " +
                "m.id as mpa_id, m.name as mpa_name, g.id as genre_id, g.name as genre_name, l.USER_ID " +
                "from films f " +
                "left join RATINGS m on f.rating_mpa_id = m.id " +
                "left join film_genres fg on f.id = fg.film_id " +
                "left join genres g on fg.genre_id = g.id " +
                "left join like_films l on f.id = l.film_id " +
                "where f.id =?" +
                "group by f.id, g.id, l.USER_ID";

        Film film = Objects.requireNonNull(jdbcTemplate.query(sqlGetById, new ListFilmExtractor(), id)).getFirst();
        return Optional.ofNullable(film);

    }

    @Override
    public List<Film> getPopularFilms(Long count, Integer genre, Integer year) {
        if (genre != null && year != null) {
            return Objects.requireNonNull(jdbcTemplate.query(sqlGetAll, new ListFilmExtractor())).stream().sorted((Film x, Film x1) -> x1.getLikeIds().size() - x.getLikeIds().size()).filter(x -> x.getReleaseDate().getYear() == year && x.getGenres().stream().anyMatch(y -> Objects.equals(y.getId(), genre))).toList();
        }
        if (genre != null) {
            return Objects.requireNonNull(jdbcTemplate.query(sqlGetAll, new ListFilmExtractor())).stream().sorted((Film x, Film x1) -> x1.getLikeIds().size() - x.getLikeIds().size()).filter(x -> x.getGenres().stream().anyMatch(y -> Objects.equals(y.getId(), genre))).toList();

        }
        if (year != null) {
            return Objects.requireNonNull(jdbcTemplate.query(sqlGetAll, new ListFilmExtractor())).stream().sorted((Film x, Film x1) -> x1.getLikeIds().size() - x.getLikeIds().size()).filter(x -> x.getReleaseDate().getYear() == year).toList();

        }
        return Objects.requireNonNull(jdbcTemplate.query(sqlGetAll, new ListFilmExtractor())).stream().sorted((Film x, Film x1) -> x1.getLikeIds().size() - x.getLikeIds().size()).limit(count).toList();
    }

    @Override
    public void addGenreToFilm(Long filmId, Integer genreId) {
        String sqlQuery = "insert into film_genres (film_id, genre_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    @Override
    public void addGenresToFilm(Long filmId, List<Genre> genres) {
        String addGenresQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (:film_id, :genre_id)";

        List<MapSqlParameterSource> batchParams = new ArrayList<>();
        for (Genre genre : genres) {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("film_id", filmId)
                    .addValue("genre_id", genre.getId());
            batchParams.add(params);
        }
        namedParameterJdbcTemplate.batchUpdate(addGenresQuery, batchParams.toArray(new MapSqlParameterSource[0]));
    }
}
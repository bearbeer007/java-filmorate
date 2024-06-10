package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowClass;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

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
        String sqlQuery = "select f.*, " +
                "m.id as mpa_id, m.name as mpa_name, " +
                "group_concat(g.id) as genre_ids, group_concat(g.name) as genre_names, " +
                "count(l.user_id) as likes " +
                "from films f " +
                "left join RATINGS m on f.rating_mpa_id = m.id " +
                "left join film_genres fg on f.id = fg.film_id " +
                "left join genres g on fg.genre_id = g.id " +
                "left join like_films l on f.id = l.film_id " +
                "group by f.id, m.id";
        ;
        return jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToFilm);
    }

    @Override
    public Optional<Film> findFilmById(Long id) {
        String sqlQuery = "select f.*, " +
                "m.id as mpa_id, m.name as mpa_name, " +
                "group_concat(g.id) as genre_ids, group_concat(g.name) as genre_names, " +
                "count(l.user_id) as likes " +
                "from films f " +
                "left join RATINGS m on f.rating_mpa_id = m.id " +
                "left join film_genres fg on f.id = fg.film_id " +
                "left join genres g on fg.genre_id = g.id " +
                "left join like_films l on f.id = l.film_id " +
                "where f.id =?" +
                "group by f.id, m.id";

        Film film = jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToFilm, id);
        return Optional.ofNullable(film);

    }

    @Override
    public List<Film> getPopularFilms(Long count, Integer genreId, Integer year) {
        String sqlQuery = "select f.id, f.name, f.description, f.release_date, f.duration " +
                "from films f " +
                "join like_films l1 on f.id = l1.film_id " +
                "left join RATINGS m on f.rating_mpa_id = m.id " +
                "left join film_genres fg on f.id = fg.film_id " +
                "left join genres g on fg.genre_id = g.id " +
                "left join like_films l2 on f.id = l2.film_id ";
        if (genreId != null && year != null) {
            String sqlAdd = "where YEAR(f.release_date) = ? AND g.id = ?" +
                    "group by f.id " +
                    "order by count(l2.user_id) desc ";
            return jdbcTemplate.query(sqlQuery + sqlAdd, MapRowClass::mapRowToFilm, year, genreId);
        }
        if (genreId != null) {
            String sqlAdd = "where g.id = ?" +
                    "group by f.id " +
                    "order by count(l2.user_id) desc ";
            return jdbcTemplate.query(sqlQuery + sqlAdd, MapRowClass::mapRowToFilm, genreId);
        }
        if (year != null) {
            String sqlAdd = "where YEAR(f.release_date) = ? " +
                    "group by f.id " +
                    "order by count(l2.user_id) desc ";
            return jdbcTemplate.query(sqlQuery + sqlAdd, MapRowClass::mapRowToFilm, year);
        }

        String sqlAdd = "group by f.id " +
                "order by count(l2.user_id) desc " +
                "limit ?";
        return jdbcTemplate.query(sqlQuery + sqlAdd, MapRowClass::mapRowToFilm, count);
    }

    @Override
    public void addGenreToFilm(Long filmId, Integer genreId) {
        String sqlQuery = "insert into film_genres (film_id, genre_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    @Override
    public void addGenresToFilm(Long filmId, List<Integer> genreIds) {
        String sqlQuery = "insert into film_genres (film_id, genre_id) values";
        StringBuilder values = new StringBuilder();
        for (Integer genreId : genreIds) {
            values.append("(").append(filmId).append(",").append(genreId).append("),");
        }
        values.setLength(values.length() - 1);
        jdbcTemplate.update(sqlQuery + values);
    }
}
package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowClass;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
        String sqlQuery = """
                SELECT f.ID,
                       f.NAME,
                       f.DESCRIPTION,
                       f.RELEASE_DATE,
                       f.DURATION,
                       f.RATING_MPA_ID,
                       r.NAME AS Mpa_name
                       FROM films AS f
                       INNER JOIN RATINGS r ON r.ID = f.RATING_MPA_ID
                        """;
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
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToFilm, id);
            return Optional.ofNullable(film);
        } catch (DataAccessException e) {
            throw new NotFoundException("Id doesn't exist. " + id);
        }
    }


    @Override
    public List<Film> getPopularFilms(Long count, Integer genreId, Integer year) {

        String sqlQuery = "select f.*" +
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

        String sqlJustCount =
                "select  f.*, rat.name AS mpa_name " +
                        "FROM films AS f " +
                        "left join ( " +
                        "select film_id, count (user_id) AS popular " +
                        "from like_films AS lf " +
                        "group by film_id " +
                        ") as pop on pop.film_id = f.id " +
                        "left join ( " +
                        "select * " +
                        "from ratings as r " +
                        ") as rat on rat.id = f.rating_mpa_id " +
                        "order by pop.popular desc " +
                        "limit ?; ";
        return jdbcTemplate.query(sqlJustCount, MapRowClass::mapRowToFilm, count);


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

    @Override
    public void deleteFilm(Long id) {
        String sqlQuery = "DELETE FROM FILMS" +
                " where id = ?";
        jdbcTemplate.update(sqlQuery, id);
        log.info("Фильм с id " + id + " успешно удален");
    }


    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {

        String sqlQuery = " SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION," +
                "f.RATING_MPA_ID, rat.NAME AS mpa_name" +
                " FROM films AS f " +
                "JOIN RATINGS AS rat ON rat.ID = f.RATING_MPA_ID " +
                "JOIN LIKE_FILMS AS l ON f.ID = l.FILM_ID " +
                "JOIN LIKE_FILMS AS lf ON l.FILM_ID = lf.FILM_ID " +
                "WHERE l.USER_ID = ? and lf.USER_ID = ?";

        return jdbcTemplate.query(sqlQuery, this::getFilmsWithGenres, userId, friendId);

    }

    private Film getFilmsWithGenres(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = MapRowClass.mapRowToFilm(resultSet, rowNum);
        film.setGenres(getAllFilmGenresById(resultSet.getLong("id")));
        return film;

    }

    private Set<Genre> getAllFilmGenresById(Long id) {
        String sqlQuery = "select * from genres where id in " +
                "(select genre_id from film_genres where film_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToGenre, id));
    }


    @Override
    public boolean contains(Long id) {
        final String sql = "SELECT EXISTS(SELECT f.id " +
                "FROM films f " +
                "WHERE f.id = ?);";
        final Boolean isExists = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExists;
    }
}
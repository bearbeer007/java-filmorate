package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.BadRequestException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowClass;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
        return jdbcTemplate.query(sqlQuery, this::getFilmsWithGenresAndMpas);
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
        return jdbcTemplate.query(sqlJustCount, this::getFilmsWithGenresAndMpas, count);
    }

  
    public List<Film> getPopular(Long count) {
        String selectPopular = "SELECT f.id, f.name, f.description, \n" +
                "                f.release_date, f.duration, \n" +
                "                mr.id AS id_rating, mr.name AS name_rating, COUNT(l.user_id) AS likesCount \n" +
                "                FROM films f \n" +
                "                LEFT JOIN ratings mr ON mr.id = f.rating_mpa_id \n" +
                "                LEFT JOIN like_films l ON l.film_id = f.id \n" +
                "                GROUP BY f.id, mr.id \n" +
                "                ORDER BY likesCount DESC";

        if (count != null) {
            selectPopular += " LIMIT " + count;
        }

        final List<Film> films = jdbcTemplate.query(selectPopular + ";", MapRowClass::mapRowToFilm2);
        final List<Long> idList = films.stream().map(Film::getId).collect(toUnmodifiableList());
        setFilmGenres(films, getFilmGenres(idList));
        setFilmDirectors(films, getFilmDirectors(idList));
        return films;
    }


    public List<Film> getPopularByGenresAndYear(Long count, Integer genreId, Integer year) {
        List<Film> filmList = getPopular(count);
        if (genreId != null) {
            filmList = filmList.stream()
                    .filter(film -> film.getGenres() != null)
                    .filter(film -> film.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
                            .contains(genreId))
                    .collect(Collectors.toList());
        }
        if (year != null) {
            filmList = filmList.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .collect(Collectors.toList());
        }
        return filmList;
    }

    private void setFilmGenres(final List<Film> films, final Map<Long, Set<Genre>> filmsGenres) {
        if (!films.isEmpty()) {
            films.forEach(f -> {
                f.setGenres(filmsGenres.get(f.getId()));
            });
        }
    }

    private void setFilmDirectors(final List<Film> films,
                                  final Map<Long, Set<Director>> filmsDirectors) {
        if (!films.isEmpty()) {
            films.forEach(f -> {
                f.setDirectors(filmsDirectors.get(f.getId()));
            });
        }
    }

    private Map<Long, Set<Genre>> getFilmGenres(final List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return new HashMap<>();
        }
        final String sql = "SELECT f.id AS id_film, g.id AS id_genre, g.name AS name_genre\n" +
                "FROM films f\n" +
                "LEFT JOIN film_genres fg ON fg.film_id = f.id\n" +
                "LEFT JOIN genres g ON g.id = fg.genre_id\n" +
                "WHERE f.id IN (:ids)\n" +
                "ORDER BY g.id;";
        final SqlParameterSource parameters = new MapSqlParameterSource("ids", filmIds);

        return namedParameterJdbcTemplate.query(sql, parameters, this::extractFilmGenres);
    }

    private Map<Long, Set<Director>> getFilmDirectors(final List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return new HashMap<>();
        }
        final String sql = "SELECT f.id AS id_film, fd.director_id AS id_director, d.name AS name_director\n" +
                "                   FROM films f\n" +
                "                   LEFT JOIN film_directors fd ON fd.film_id = f.id\n" +
                "                   LEFT JOIN directors d ON d.id = fd.director_id\n" +
                "                   WHERE f.id IN (:ids) ORDER BY d.id;";
        final SqlParameterSource parameters = new MapSqlParameterSource("ids", filmIds);

        return namedParameterJdbcTemplate.query(sql, parameters, this::extractFilmDirectors);
    }

    private Map<Long, Set<Genre>> extractFilmGenres(final ResultSet rs) throws SQLException {
        final Map<Long, Set<Genre>> resultMap = new HashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("id_film");
            final Genre genre = getGenreFromResultSet(rs);

            // если жанров у фильма нет, то должен быть пустой список
            if (!resultMap.containsKey(filmId)) {
                resultMap.put(filmId, new LinkedHashSet<>());
            }

            if (nonNull(genre)) {
                resultMap.get(filmId).add(genre);
            }
        }

        return resultMap;
    }

    private Map<Long, Set<Director>> extractFilmDirectors(final ResultSet rs) throws SQLException {
        final Map<Long, Set<Director>> resultMap = new HashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("id_film");
            final Director director = getDirectorFromResultSet(rs);

            // если режиссеров у фильма нет, то должен быть пустой список
            if (!resultMap.containsKey(filmId)) {
                resultMap.put(filmId, new LinkedHashSet<>());
            }

            if (nonNull(director)) {
                resultMap.get(filmId).add(director);
            }
        }

        return resultMap;
    }

    private Director getDirectorFromResultSet(final ResultSet rs) throws SQLException {
        Director director = null;

        Long directorId = rs.getLong("id_director");
        if (!rs.wasNull()) {
            director = Director.builder()
                    .id(directorId)
                    .name(rs.getString("name_director"))
                    .build();
        }

        return director;
    }


    private Genre getGenreFromResultSet(final ResultSet rs) throws SQLException {
        Genre genre = null;

        int genreId = rs.getInt("id_genre");
        if (!rs.wasNull()) {
            genre = Genre.builder()
                    .id(genreId)
                    .name(rs.getString("name_genre"))
                    .build();
        }

        return genre;
    }


    @Override
    public void addGenreToFilm(Long filmId, Integer genreId) {
        String sqlQuery = "merge into film_genres(genre_id, film_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, genreId, filmId);
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
    private Film getFilmsWithGenresAndMpas(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = MapRowClass.mapRowToFilm(resultSet, rowNum);
        film.setGenres(getAllFilmGenresById(resultSet.getLong("id")));
        Optional<Mpa> test = findRatingByFilmId(resultSet.getLong("id"));
        film.setMpa(test.get());
        return film;

    }

    private Set<Genre> getAllFilmGenresById(Long id) {
        String sqlQuery = "select * from genres where id in " +
                "(select genre_id from film_genres where film_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToGenre, id));
    }
    public Optional<Mpa> findRatingByFilmId(Long id) {
        String sqlQuery = "select r.id, r.name from films f join ratings r on f.rating_mpa_id = r.id where f.id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToMpa, id);
            return Optional.ofNullable(mpa);
        } catch (DataAccessException e) {
            throw new NotFoundException("Mpa with film id:" + id + "doesn't exist.");
        }
    }

    @Override
    public boolean contains(Long id) {
        final String sql = "SELECT EXISTS(SELECT f.id " +
                "FROM films f " +
                "WHERE f.id = ?);";
        final Boolean isExists = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExists;
    }

    @Override
    public List<Long> findSimilarUsersByLikes(Long userId) {
        String sql = "SELECT lf1.user_id, COUNT(*) AS common_likes " +
                "FROM like_films AS lf1 " +
                "JOIN like_films AS lf2 ON lf1.film_id = lf2.film_id " +
                "WHERE lf2.user_id = ? AND lf1.user_id != ? " +
                "GROUP BY lf1.user_id " +
                "ORDER BY common_likes DESC";

        return jdbcTemplate.query(sql, new Object[]{userId, userId},
                (rs, rowNum) -> rs.getLong("user_id"));
    }

    @Override
    public List<Film> findRecommendedFilms(Long userId, Long similarUserId) {
        String sqlQuery = "SELECT * " +
                "FROM like_films AS lf " +
                "JOIN films AS f ON lf.film_id = f.id " +
                "WHERE user_id = ? " +
                "AND film_id NOT IN (SELECT film_id FROM like_films WHERE user_id = ?)";

        return jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToFilm, similarUserId, userId);
    }

    @Override
    public List<Film> searchFilmsByTitleAndDirector(String query, String by) {
        String sqlQuery;
        return switch (by) {
            case "title" -> {
                sqlQuery = "select f.* from films as f where f.name like concat('%',?,'%')";
                yield jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToFilm, query);
            }
            case "director" -> {
                sqlQuery = "select f.* from films as f " +
                        "inner join film_directors as fd on f.id = fd.film_id " +
                        "inner join directors as d on d.id = fd.director_id " +
                        "where d.name like concat('%',?,'%')";
                yield jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToFilm, query);
            }
            case "title,director", "director,title" -> {
                String sql = "select fl.* from films as fl " +
                        "where fl.name like concat('%',?,'%')";
                List<Film> filmsOne = jdbcTemplate.query(sql, MapRowClass::mapRowToFilm, query);

                sql = "select f.* from films as f where f.id in " +
                        "(select fd.film_id from film_directors as fd " +
                        "inner join directors as d on fd.director_id = d.id " +
                        "where d.name like concat('%',?,'%'))";
                List<Film> filmsTwo = jdbcTemplate.query(sql, MapRowClass::mapRowToFilm, query);
                yield Stream.concat(filmsOne.stream(), filmsTwo.stream()).collect(Collectors.toList());
            }
            default -> throw new BadRequestException("Передан неверный запрос");
        };
    }
}
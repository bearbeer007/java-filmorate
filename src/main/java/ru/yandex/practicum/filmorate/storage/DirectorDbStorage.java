package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.BadRequestException;
import ru.yandex.practicum.filmorate.exeption.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowClass;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> objectUser = new HashMap<>();
        objectUser.put("name", director.getName());
        Number id = simpleJdbcInsert.executeAndReturnKey(objectUser);
        director.setId(id.longValue());
        log.info("Director {} create", director.getName());
        return director;
    }

    @Override
    public List<Director> getAllDirectors() {
        String sqlQuery = "select * from directors";
        log.info("Get List of directors");
        return jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToDirector);
    }

    @Override
    public Optional<Director> findDirectorById(Long id) {
        String sqlQuery = "select * from directors where id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            Director director = Director.builder()
                            .id(filmRows.getLong("id"))
                            .name(filmRows.getString("name"))
                            .build();
            log.info("Director with ID {}: {}", id, director.getName());
            return Optional.of(director);
        }
        return Optional.empty();
    }

    @Override
    public Director updateDirector(Director director) {
        Optional<Director> optionalDirector = findDirectorById(director.getId());
        if (optionalDirector.isEmpty()) {
            log.info("Director with id: {} not found.", director.getId());
            throw new DirectorNotFoundException("Director with id: {} not found.");
        } else {
            String sqlQuery = "update directors set name = ? where id = ?";
            jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
            log.info("Director information updated");
        }
        return director;
    }

    @Override
    public Optional<Director> removeDirectorById(Long id) {
        Optional<Director> director = findDirectorById(id);
        if (director.isPresent()) {
            String sql = "delete from films where id = ?";
            jdbcTemplate.update(sql, id);
            log.info("Deleted");
            return director;
        } else {
            log.info("Director with id: {} not found", id);
            return Optional.empty();
        }
    }

    @Override
    public Set<Director> getDirectorsFilm(Long id) {
        Set<Director> filmDirectors = new HashSet<>();
        String sql = "select * from directors as d " +
                     "inner join film_directors AS fd ON d.id = fd.director_id " +
                     "where fd.film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (filmRows.next()) {
            Director director = Director.builder()
                    .id(filmRows.getLong("id"))
                    .name(filmRows.getString("name"))
                    .build();
            filmDirectors.add(director);
        }
        return new HashSet<>(filmDirectors);
    }

    @Override
    public void setDirectorToFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_directors");
        Map<String, Object> objectUser = new HashMap<>();
        for (Director director : film.getDirectors()) {
            objectUser.put("film_id", film.getId());
            objectUser.put("director_id", director.getId());
        }
        simpleJdbcInsert.execute(objectUser);
        log.info("Director added to the film");
    }

    @Override
    public List<Film> getFilmsSortByYearOrLikes(Long id, String obj) {
        String sqlQuery;
        return switch (obj) {
            case "likes" -> {
                sqlQuery = "select f.* from films as f " +
                           "where f.id in " +
                           "(select fd.film_id from film_directors as fd " +
                           "inner join directors as d on fd.director_id = d.id " +
                           "inner join like_films as lf on fd.film_id = lf.film_id " +
                           "where fd.director_id = ? " +
                           "group by fd.film_id " +
                           "order by count(lf.user_id) DESC)";
                yield getSortFilms(id, sqlQuery);
            }
            case "year" -> {
                sqlQuery = "select f.* from films as f " +
                           "inner join film_directors as fd on f.id = fd.film_id " +
                           "where fd.director_id = ? " +
                           "group by f.id, f.release_date " +
                           "order by extract(year from CAST(f.release_date AS date)) ASC";
                yield getSortFilms(id, sqlQuery);
            }
            default -> throw new BadRequestException( "Передан неверный запрос");
        };
    }

    private List<Film> getSortFilms(Long id, String query) {
        return jdbcTemplate.query(query, MapRowClass::mapRowToFilm, id).stream()
                .map(film -> film.toBuilder()
                        .directors(getDirectorsFilm(film.getId().longValue()))
                        .build())
                .collect(Collectors.toList());
    }
}

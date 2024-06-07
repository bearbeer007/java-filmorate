package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowClass;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Genre> getAllGenres() {
        String sqlQuery = "select * from genres order by id";
        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToGenre));
    }

    @Override
    public Optional<Genre> findGenreById(Integer id) {
        String sqlQuery = "select * from genres where id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToGenre, id);
            return Optional.ofNullable(genre);
        } catch (DataAccessException e) {
            throw new NotFoundException("Id doesn't exist." + id);
        }
    }

    @Override
    public Set<Genre> getAllGenresByFilm(Long id) {
        String sqlQuery = "select * from genres where id in " +
                "(select genre_id from film_genres where film_id = ?)";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToGenre, id));
    }
}

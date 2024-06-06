package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllRatings() {
        String sqlQuery = "select * from ratings order by id";
        return jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToMpa);
    }

    @Override
    public Optional<Mpa> findRatingById(Integer id) {
        String sqlQuery = "select * from ratings where id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToMpa, id);
            return Optional.ofNullable(mpa);
        } catch (DataAccessException e) {
            throw new NotFoundException("Mpa id doesn't exist." + id);
        }
    }

    @Override
    public Optional<Mpa> findRatingByFilmId(Long id) {
        String sqlQuery = "select r.id, r.name from films f join ratings r on f.rating_mpa_id = r.id where f.id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToMpa, id);
            return Optional.ofNullable(mpa);
        } catch (DataAccessException e) {
            throw new NotFoundException("Mpa with film id:" + id + "doesn't exist.");
        }
    }
}

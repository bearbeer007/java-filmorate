package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.LikesStorage;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;

    @Override
    public Set<Long> getLikes(Long id) {
        String sqlQueryLikes = "select user_id from like_films where film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQueryLikes, (rs, rn) -> rs.getLong("user_id"), id));
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        String sqlQuery = "insert into like_films (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return filmStorage.findFilmById(filmId).get();
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sqlQuery = "delete from like_films where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

}

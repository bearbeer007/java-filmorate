package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ListFilmExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {
        List<Film> films = new ArrayList<>();
        Map<Long, Film> filmMap = new HashMap<>();

        while (rs.next()) {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);

            if (film == null) {
                film = Film.builder()
                        .id(filmId)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .mpa(Mpa.builder()
                                .id((int) rs.getLong("mpaId"))
                                .name(rs.getString("mpaName")).build())
                        .genres(new HashSet<>())
                        .likeIds(new HashSet<>())
                        .build();

                filmMap.put(filmId, film);
                films.add(film);
            }

            if (rs.getLong("genreId") != 0) {
                film.getGenres().add(Genre.builder()
                        .id((int) rs.getLong("genreId"))
                        .name(rs.getString("genreName")).build());
            }
            if (rs.getLong("user_id") != 0) {
                film.getLikeIds().add(rs.getLong("user_id"));

            }
        }

        return films;
    }
}
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
            Long filmId = rs.getLong("id");
            Film film = filmMap.get(filmId);

            if (film == null) {
                film = new Film();
                film.setId(filmId);
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setMpa(Mpa.builder().id(rs.getInt("mpa_id")).name( rs.getString("mpa_name")).build());
                film.setGenres(new HashSet<>());

                filmMap.put(filmId, film);
                films.add(film);
            }

            if (rs.getLong("genre_id") != 0) {
                film.getGenres().add(Genre.builder().id(rs.getInt("genre_id")).name(rs.getString("genre_name")).build());
            }
            if (rs.getLong("user_id") != 0) {
                film.getLikeIds().add(rs.getLong("user_id"));

            }
        }

        return films;
    }
}

package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
public class MapRowClass {

    public static Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }

    public static Film mapRowToFilm2(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa rating = null;
        int ratingId = resultSet.getInt("id_rating");
        if (!resultSet.wasNull()) {
            rating = Mpa.builder()
                    .id(ratingId)
                    .name(resultSet.getString("name_rating"))
                    .build();
        }

        final Film film = Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(rating)
                .build();

        return film;
    }


    public static Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public static Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public static User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    public static Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .id(resultSet.getLong("id"))
                .userId(resultSet.getLong("id_user"))
                .filmId(resultSet.getLong("id_film"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("isPositive"))
                .rating(resultSet.getInt("rating"))
                .build();
    }

    public static Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getLong("id"))
                .entityId(resultSet.getLong("entity_id"))
                .userId(resultSet.getLong("id_user"))
                .eventType(EventType.valueOf(resultSet.getString("event_type")))
                .operation(Operation.valueOf(resultSet.getString("operation")))
                .timestamp(resultSet.getDate("last_update").getTime())
                .build();
    }

    public static Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public static Event mapRowToEvent(Long userId, Long entityId, EventType eventType, Operation operation) {
        return Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .build();
    }
}

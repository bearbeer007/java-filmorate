package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowClass;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getEventsByUserId(Long id) {
        String sql = "SELECT id, last_update, id_user, event_type, operation, entity_id " +
                "FROM events " +
                "WHERE id_user = ?";

        return jdbcTemplate.query(sql, MapRowClass::mapRowToEvent, id);
    }

    @Override
    public Event addEvent(Event event) {
        String sql = "INSERT INTO events (id_user, event_type, operation, entity_id, last_update) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, event.getUserId());
            stmt.setString(2, event.getEventType().name());
            stmt.setString(3, event.getOperation().name());
            stmt.setLong(4, event.getEntityId());
            return stmt;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        event.setEventId(id);

        return event;
    }
}
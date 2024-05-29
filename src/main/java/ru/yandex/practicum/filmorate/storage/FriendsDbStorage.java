package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendsStorage;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Long> getFriends(Long id) {
        String sql = "select friend_id from users_friendship where user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rn) -> rs.getLong("friend_id"), id));
    }
}

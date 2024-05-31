package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendsStorage;

import java.util.HashSet;
import java.util.List;
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

    @Override
    public User addFriend(Long userId, Long friendId) {
        String sqlQuery = "insert into users_friendship (user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        return findUserById(userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "delete from users_friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getFriendsList(Long id) {
        String sqlQuery = "select * from users where id in " +
                "(select friend_id from users_friendship where user_id = ?)";
        return jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToUser, id);
    }

    @Override
    public List<User> commonFriends(Long userFirst, Long userSecond) {
        String sqlQuery = "select * from users where id in " +
                "(select friend_id from users_friendship where user_id = ?) " +
                "and id in (select friend_id from users_friendship where user_id = ?)";
        return jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToUser, userFirst, userSecond);
    }

    private User findUserById(Long id) {
        String sqlQuery = "select * from users where id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToUser, id);
        } catch (DataAccessException e) {
            throw new NotFoundException("User with id " + id + " not found.");
        }
    }
}

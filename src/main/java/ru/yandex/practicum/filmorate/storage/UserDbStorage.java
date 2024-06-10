package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(toMap(user)).longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where id = ?";
        if (jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()) == 0) {
            log.info("User with id: {} not found.", user.getId());
            throw new NotFoundException("Wrong user ID" + user.getId());
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, MapRowClass::mapRowToUser);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        String sqlQuery = "select * from users where id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, MapRowClass::mapRowToUser, id);
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            throw new NotFoundException("Id doesn't exist. " + id);
        }
    }


    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("id", user.getId());
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    @Override
    public void deleteUser(Long id) {
        String sqlQuery = "DELETE FROM users" +
                " where id = ?";
        int update = jdbcTemplate.update(sqlQuery, id);
        if (update == 0) {
            throw new NotFoundException("Пользователь с id =  " + id + " не найден");
        }
        log.info("Пользователь с id " + id + " успешно удален");
    }

    @Override
    public boolean contains(Long id) {
        final String sql = "SELECT EXISTS(SELECT id " +
                "FROM USERS " +
                "WHERE id = ?);";
        final Boolean isExists = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExists;
    }
}
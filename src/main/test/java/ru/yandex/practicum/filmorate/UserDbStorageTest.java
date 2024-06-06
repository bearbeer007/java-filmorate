package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserById() {
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.addUser(newUser);

        if (userStorage.findUserById(1L).isPresent()) {
            User savedUser = userStorage.findUserById(1L).get();
            assertThat(savedUser)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(newUser);
        }
    }

    @Test
    public void testGetAllUsers() {
        User newUser1 = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User newUser2 = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("Name Mayers")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();

        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.addUser(newUser1);
        userStorage.addUser(newUser2);
        List<User> users = userStorage.getAllUsers();
        assertThat(users)
                .isNotNull()
                .contains(newUser1)
                .contains(newUser2)
                .usingRecursiveComparison();
        assertEquals(2, users.size());
    }

    @Test
    public void testUpdateUser() {
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.addUser(newUser);

        newUser.setName("newUserName");
        newUser.setLogin("newUserLogin");
        userStorage.updateUser(newUser);

        if (userStorage.findUserById(1L).isPresent()) {
            User savedUser = userStorage.findUserById(1L).get();
            assertThat(savedUser)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(newUser);
        }
    }

    @Test
    public void testAddFriend() {
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User createdUser = userStorage.addUser(newUser);
        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);


        User newUser2 = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("Name Mayers")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        User createdFriend = userStorage.addUser(newUser2);

        friendsDbStorage.addFriend(createdUser.getId(), createdFriend.getId());
        assertTrue(friendAdded(createdUser.getId(), createdFriend.getId()));
    }

    @Test
    public void testDeleteFriend() {
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        User createdUser = userStorage.addUser(newUser);
        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);


        User newUser2 = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("Name Mayers")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        User createdFriend = userStorage.addUser(newUser2);
        friendsDbStorage.addFriend(createdUser.getId(), createdFriend.getId());
        assertTrue(friendAdded(createdUser.getId(), createdFriend.getId()));
        friendsDbStorage.deleteFriend(createdUser.getId(), createdFriend.getId());
        assertTrue(friendRemoved(createdUser.getId(), createdFriend.getId()));
    }

    @Test
    public void testGetFriends() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);
        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User userFriend1 = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("Name Mayers")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        User userFriend2 = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("Name Mayers")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();

        var createdUser = userStorage.addUser(newUser);
        var createdFriend1 = userStorage.addUser(userFriend1);
        var createdFriend2 = userStorage.addUser(userFriend2);

        friendsDbStorage.addFriend(createdUser.getId(), createdFriend1.getId());
        friendsDbStorage.addFriend(createdUser.getId(), createdFriend2.getId());
        List<User> users = friendsDbStorage.getFriendsList(createdUser.getId());

        List<User> friends = new ArrayList<>();
        friends.add(createdFriend1);
        friends.add(createdFriend2);

        assertEquals(2, users.size());
        assertEquals(users, friends);
        assertTrue(users.contains(createdFriend1));
        assertTrue(users.contains(createdFriend2));
    }

    @Test
    public void testCommonFriends() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        FriendsDbStorage friendsDbStorage = new FriendsDbStorage(jdbcTemplate);

        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User newUser2 = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User userFriend1 = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("Name Mayers")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        User userFriend2 = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("Name Mayers")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        User userFriend3 = User.builder()
                .email("email@email.ru")
                .login("login")
                .name("Name Mayers")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();

        var createdUser1 = userStorage.addUser(newUser);
        var createdUser2 = userStorage.addUser(newUser2);
        var createdFriend1 = userStorage.addUser(userFriend1);
        var createdFriend2 = userStorage.addUser(userFriend2);
        var createdFriend3 = userStorage.addUser(userFriend3);

        friendsDbStorage.addFriend(createdUser1.getId(), createdFriend1.getId());
        friendsDbStorage.addFriend(createdUser1.getId(), createdFriend2.getId());
        friendsDbStorage.addFriend(createdUser1.getId(), createdFriend3.getId());
        friendsDbStorage.addFriend(createdUser2.getId(), createdFriend2.getId());
        friendsDbStorage.addFriend(createdUser2.getId(), createdFriend1.getId());
        List<User> users = friendsDbStorage.commonFriends(createdUser1.getId(), createdUser2.getId());

        List<User> friends = new ArrayList<>();
        friends.add(createdFriend1);
        friends.add(createdFriend2);

        assertEquals(2, users.size());
        assertEquals(users, friends);
        assertTrue(users.contains(createdFriend1));
        assertTrue(users.contains(createdFriend2));
    }

    private boolean friendAdded(Long userId, Long friendId) {
        String sql = "SELECT COUNT(1) FROM users_friendship where user_id = ? and friend_id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, friendId));
    }

    private boolean friendRemoved(Long userId, Long friendId) {
        String sql = "SELECT COUNT(1) FROM users_friendship WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId) == 0;
    }
}

package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User addUser(User user) {
        var createdUser = userStorage.addUser(user);
        checkName(user);
        return createdUser;
    }

    @Override
    public User updateUser(User user) {
        var updatedUser = userStorage.updateUser(user);
        checkName(updatedUser);
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.getUserById(id).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с таким id: %s, отсутствует", id)));
    }

    @Override
    public List<User> addFriend(Long userId, Long friendId) {
        if (!userId.equals(friendId)) {
            Optional<User> optionalUser1 = userStorage.getUserById(userId);
            Optional<User> optionalFriend2 = userStorage.getUserById(friendId);
            User u1 = optionalUser1.get();
            User u2 = optionalFriend2.get();
            u1.getFriends().add(u2.getId());
            u2.getFriends().add(u1.getId());
            return List.of(u1, u2);
        }
        throw new ValidationException("Identical IDs. The user cannot add himself as a friend.");
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        var user = getUserById(userId);
        var friend = getUserById(friendId);
        user.getFriendsIds().remove(friendId);
        friend.getFriendsIds().remove(userId);
    }

    @Override
    public List<User> getFriends(Long id) {
        var user = getUserById(id);
        List<User> userFriends = new ArrayList<>();
        for (Long friendsId : user.getFriendsIds()) {
            if (getUserById(friendsId) != null) {
                userFriends.add(getUserById(friendsId));
            } else {
                throw new NotFoundException("User id doesn't exist");
            }
        }
        return userFriends;
    }

    @Override
    public List<Optional<User>> commonFriends(Long userFirst, Long userSecond) {
        if (!userFirst.equals(userSecond)) {
            Optional<User> optionalUserFirst = userStorage.getUserById(userFirst);
            Optional<User> optionalUserSecond = userStorage.getUserById(userSecond);
            User u1 = optionalUserFirst.get();
            User u2 = optionalUserSecond.get();
            u1.getFriends().add(u2.getId());
            u2.getFriends().add(u1.getId());

            Set<Long> common = new HashSet<>(u1.getFriends());
            common.retainAll(u2.getFriends());
            List<Optional<User>> commonFriends = new ArrayList<>();
            for (Long id : common) {
                commonFriends.add(userStorage.getUserById(id));
            }
            return commonFriends;
        }
        throw new ValidationException("Identical IDs.");
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
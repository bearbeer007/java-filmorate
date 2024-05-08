package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public User addFriend(Long userId, Long friendId) {
        var user = getUserById(userId);
        var friend = getUserById(friendId);
        user.getFriendsIds().add(friendId);
        friend.getFriendsIds().add(userId);
        return user;
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
    public List<User> commonFriends(Long userFirst, Long userSecond) {
        var user = getUserById(userFirst);
        var friend = getUserById(userSecond);

        Set<Long> userFriendsIds = user.getFriendsIds();
        Set<Long> friendFriendsIds = friend.getFriendsIds();
        Set<Long> commonFriends = new HashSet<>(userFriendsIds);
        commonFriends.retainAll(friendFriendsIds);

        List<User> commonUsers = new ArrayList<>();
        for (Long commonFriendId : commonFriends) {
            if (getUserById(commonFriendId) != null) {
                commonUsers.add(getUserById(commonFriendId));
            } else {
                throw new NotFoundException("User id doesn't exist");
            }
        }
        return commonUsers;
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.services.interfaces.FriendsService;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.storage.mapper.MapRowClass;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final FriendsStorage friendsStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    public Set<Long> getFriends(Long id) {
        return friendsStorage.getFriends(id);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        validateDifferentIds(userId, friendId);
        userStorage.findUserById(userId);
        userStorage.findUserById(friendId);
        eventStorage.addEvent(MapRowClass.mapRowToEvent(userId, friendId, EventType.FRIEND, Operation.ADD));
        return friendsStorage.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        validateDifferentIds(userId, friendId);
        userStorage.findUserById(userId);
        userStorage.findUserById(friendId);
        friendsStorage.deleteFriend(userId, friendId);
        eventStorage.addEvent(MapRowClass.mapRowToEvent(userId, friendId, EventType.FRIEND, Operation.REMOVE));
    }

    @Override
    public List<User> getFriendsList(Long id) {
        userStorage.findUserById(id);
        return friendsStorage.getFriendsList(id);
    }

    @Override
    public List<User> commonFriends(Long userFirst, Long userSecond) {
        validateDifferentIds(userFirst, userSecond);
        userStorage.findUserById(userFirst);
        userStorage.findUserById(userSecond);
        return friendsStorage.commonFriends(userFirst, userSecond);
    }

    private void validateDifferentIds(Long id1, Long id2) {
        if (id1.equals(id2)) {
            throw new ValidationException("Identical IDs. The user cannot add himself as a friend.");
        }
    }
}

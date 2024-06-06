package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.BadRequestException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.interfaces.LikesService;
import ru.yandex.practicum.filmorate.services.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.interfaces.LikesStorage;


import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

    private final LikesStorage likesStorage;
    private final UserService userService;


    @Override
    public Set<Long> getLikes(Long id) {
        return likesStorage.getLikes(id);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        userService.getUserById(userId);
        if (getLikes(filmId).contains(userId)) {
            throw new BadRequestException("One user - one like, exceeded the allowed number of likes");

        }
        return likesStorage.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        userService.getUserById(userId);

        if (!likesStorage.getLikes(filmId).contains(userId)) {
            throw new NotFoundException(
                    String.format("No like from user with id - %s to film with id - %s", userId, filmId));
        }
        likesStorage.deleteLike(filmId, userId);
    }
}

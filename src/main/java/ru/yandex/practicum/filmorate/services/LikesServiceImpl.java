package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.services.interfaces.LikesService;
import ru.yandex.practicum.filmorate.storage.interfaces.LikesStorage;


import java.util.Set;

@Service
@RequiredArgsConstructor
public class LikesServiceImpl implements LikesService {

    private final LikesStorage likesStorage;

    @Override
    public Set<Long> getLikes(Long id) {
        return likesStorage.getLikes(id);
    }
}

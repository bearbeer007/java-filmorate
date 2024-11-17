package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.services.interfaces.MpaService;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public List<Mpa> getAllMpaRatings() {
        return mpaStorage.getAllRatings();
    }

    @Override
    public Mpa getMpaRatingById(Integer id) {
        return mpaStorage.findRatingById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Передан отсутствующий Mpa с id: %s.", id)));
    }

}

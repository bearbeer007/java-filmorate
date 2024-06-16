package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.services.interfaces.GenreService;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public Set<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    @Override
    public Genre getGenreById(Integer id) {
        return genreStorage.findGenreById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Жанр с таким id: %s, отсутствует", id)));
    }

}

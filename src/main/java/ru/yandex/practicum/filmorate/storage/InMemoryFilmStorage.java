package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long idGenerator = 1;

    @Override
    public Film addFilm(Film filmFromRequest) {
        Film film = filmFromRequest.toBuilder()
                .id(idGenerator++)
                .build();
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
            return film;
        } else {
            throw new NotFoundException(String.format("Film with id=%d was not found.", film.getId()));
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        if (films.containsKey(id)) {
            return Optional.ofNullable(films.get(id));
        } else {
            throw new NotFoundException(String.format("Film with id=%d was not found.", id));
        }
    }
}

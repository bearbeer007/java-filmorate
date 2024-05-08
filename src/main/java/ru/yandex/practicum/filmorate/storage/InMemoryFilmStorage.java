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

    private final Map<Integer, Film> films = new HashMap<>();
    private int idGenerator = 1;

    @Override
    public Film addFilm(Film film) {
        film.setId(idGenerator);
        log.info("Фильм добавлен, присвоен номер id: {}.", film.getId());
        films.put(film.getId(), film);
        idGenerator++;
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            log.info("Фильм с номером id: {}, обновлен.", film.getId());
            films.put(film.getId(), film);
        } else {
            log.info("Фильм с таким id: {}, отсутствует", film.getId());
            throw new NotFoundException(String.format("Фильм с таким id: %s, отсутствует", film.getId()));
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }
}

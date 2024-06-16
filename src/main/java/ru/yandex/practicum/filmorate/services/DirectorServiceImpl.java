package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.services.interfaces.DirectorService;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public Director addDirector(Director director) {
        log.info("Request to add a new director: {}", director.toString());
        return directorStorage.addDirector(director);
    }

    @Override
    public List<Director> getAllDirectors() {
        log.info("Request to display a list of directors");
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director findDirectorById(Long id) {
        log.info("Search director with ID {}", id);
        return directorStorage.findDirectorById(id)
                .orElseThrow(() -> new NotFoundException("The given ID is not found"));
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("Director data update request");
        return directorStorage.updateDirector(director);
    }

    @Override
    public String removeDirectorById(Long id) {
        Optional<Director> director = directorStorage.removeDirectorById(id);
        if (director.isPresent()) {
            return "Удалено: " + director.get().toString();
        } else {
            return "Режиссер с ID " + id + " не найден или был удален ранее";
        }
    }
}

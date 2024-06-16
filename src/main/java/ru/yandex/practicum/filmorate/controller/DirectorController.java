package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.services.interfaces.DirectorService;
import ru.yandex.practicum.filmorate.validator.interfaces.Marker;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("Запрос на добалвение режиссеров {}", director);
        return directorService.addDirector(director);
    }

    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable("id") Long id) {
        log.info("Запрос на поиск режиссера с id {}", id);
        return directorService.findDirectorById(id);
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("Запрос на получение всех режиссеров");
        return directorService.getAllDirectors();
    }

    @PutMapping
    public Director updateDirector(@Validated(Marker.onUpdate.class) @RequestBody Director director) {
        log.info("Запрос на обноление режиссера");
        return directorService.updateDirector(director);
    }

    @DeleteMapping(value = "/{id}")
    public String removeDirectorById(@PathVariable("id") Long id) {
        log.info("Запрос на удаление режиссера с id {}", id);
        return directorService.removeDirectorById(id);
    }
}

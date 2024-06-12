package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.services.interfaces.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        return directorService.addDirector(director);
    }

    @GetMapping("/{id}")
    public Director findDirectorById(@PathVariable("id") Long id) {
        return directorService.findDirectorById(id);
    }

    @GetMapping
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping(value = "/{id}")
    public String removeDirectorById(@PathVariable("id") Long id) {
        return directorService.removeDirectorById(id);
    }
}

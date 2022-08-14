package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Film with id = " + film.getId() + " added");
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        if (!films.containsKey(film.getId())) {
            throw new IdNotFoundException("Film with id = " + film.getId() + " not found");
        }
        films.put(film.getId(), film);
        log.info("Film with id = " + film.getId() + " updated");
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilm() {
        return new ArrayList<>(films.values());
    }
}

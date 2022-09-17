package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final long id = 0;

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        return filmStorage.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        return filmStorage.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilm() {
        return filmStorage.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmStorage.getFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getTopFilms(@RequestParam Optional<Integer> count) {
        return filmService.getTopRatedFilms(count.orElse(10));

    }
}

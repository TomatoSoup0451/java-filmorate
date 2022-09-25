package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    @Override
    public Film createFilm(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Film with id = " + film.getId() + " added");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new IdNotFoundException("Film with id = " + film.getId() + " not found");
        }
        films.put(film.getId(), film);
        log.info("Film with id = " + film.getId() + " updated");
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(long id) {
        if (!films.containsKey(id)) {
            throw new IdNotFoundException("Film with id = " + id + " not found");
        }
        return films.get(id);
    }

    @Override
    public List<Film> getMultipleFilms(long... id) {
        List<Film> result = new ArrayList<>();
        for (long i : id) {
            if (!films.containsKey(i)) {
                throw new IdNotFoundException("Film with id = " + i + " not found");
            }
            result.add(films.get(i));
        }
        return result;
    }
}

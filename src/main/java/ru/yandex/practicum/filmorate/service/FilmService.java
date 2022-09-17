package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public final void addLike(long filmId, long userId) {
        filmStorage.getFilm(filmId).addLike(userStorage.getUser(userId));
        log.info("User with id " + userId + " added like to film with id " + filmId);
    }

    public final void removeLike(long filmId, long userId) {
        filmStorage.getFilm(filmId).removeLike(userStorage.getUser(userId));
        log.info("User with id " + userId + " removed like from film with id " + filmId);
    }

    public final List<Film> getTopRatedFilms(int count) {
        return filmStorage.getAllFilms().stream().
                sorted(((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())).
                limit(count).
                collect(Collectors.toList());
    }
}

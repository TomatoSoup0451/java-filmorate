package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/test_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/drop_test_data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void shouldReturnUserWithId1() {
        User user = userStorage.getUser(101);
        assertEquals(101, user.getId(), "User id is wrong");
    }

    @Test
    public void shouldReturnListOf3Users() {
        List<User> users = userStorage.getAllUsers();
        assertEquals(3, users.size(), "Number of users incorrect");
    }

    @Test
    public void shouldThrowIdNotFoundExceptionWhenUserIdMinus1() {
        final IdNotFoundException e = assertThrows(IdNotFoundException.class,
                () -> userStorage.getUser(-1));
        assertEquals("User with id = -1 not found", e.getMessage());
    }

    @Test
    public void shouldReturnNameChangedWhenUser101Updated() {
        User newUser = User.builder()
                .id(101)
                .name("Changed")
                .email("a@ya.ru")
                .birthday(LocalDate.of(1990, 7, 27))
                .login("l1")
                .build();
        userStorage.updateUser(newUser);
        User user = userStorage.getUser(101);
        assertEquals("Changed", user.getName(), "User name is wrong");
    }

    @Test
    public void shouldAddUserWithId1() {
        User newUser = User.builder()
                .name("n4")
                .email("d@ya.ru")
                .birthday(LocalDate.of(4990, 7, 27))
                .login("l4")
                .build();
        userStorage.createUser(newUser);
        User user = userStorage.getUser(1);
        assertEquals("n4", user.getName(), "User name is wrong");
    }

    @Test
    public void shouldAddFriend102toUser101AndThenRemove() {
        userStorage.updateFriendship(userStorage.getUser(101), userStorage.getUser(102), true);
        User user = userStorage.getUser(101);
        assertTrue(user.getFriends().containsKey(102L), "User 102 is not in user 101 friendlist");
        userStorage.updateFriendship(userStorage.getUser(101), userStorage.getUser(102), false);
        user = userStorage.getUser(101);
        assertFalse(user.getFriends().containsKey(102L), "User 102 is not removed from user 101 friendlist");
    }

    @Test
    public void shouldReturnFilmWithId101() {
        Film film = filmStorage.getFilm(101);
        assertEquals(101, film.getId(), "Film id is wrong");
    }

    @Test
    public void shouldReturnListOf3Films() {
        List<Film> films = filmStorage.getAllFilms();
        assertEquals(3, films.size(), "Number of films incorrect");
    }

    @Test
    public void shouldThrowIdNotFoundExceptionWhenUserMinus1() {
        final IdNotFoundException e = assertThrows(IdNotFoundException.class,
                () -> filmStorage.getFilm(-1));
        assertEquals("Film with id = -1 not found", e.getMessage());
    }

    @Test
    public void shouldReturnNameChangedWhenFilm101Updated() {
        Film newFilm = Film.builder()
                .id(101)
                .name("Changed")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .description("d1")
                .mpa(new Mpa(1, "G"))
                .build();
        filmStorage.updateFilm(newFilm);
        Film film = filmStorage.getFilm(101);
        assertEquals("Changed", film.getName(), "Film name is wrong");
    }

    @Test
    public void shouldAddFilmWithId1() {
        Film newFilm = Film.builder()
                .name("n4")
                .releaseDate(LocalDate.of(5000, 1, 1))
                .duration(400)
                .description("d4")
                .mpa(new Mpa(1, "G"))
                .build();
        filmStorage.createFilm(newFilm);
        Film film = filmStorage.getFilm(1);
        assertEquals("n4", film.getName(), "Film name is wrong");
    }
}

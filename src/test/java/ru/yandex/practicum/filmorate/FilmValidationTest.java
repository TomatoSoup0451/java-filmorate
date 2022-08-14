package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
public class FilmValidationTest {

    private final Validator validator;

    @Autowired
    public FilmValidationTest(Validator validator) {
        this.validator = validator;
    }

    @Test
    void shouldAcceptValidFilm() {
        Film film = Film.builder()
                .id(1L)
                .description("Description")
                .duration(100)
                .releaseDate(LocalDate.of(1990, 7, 27))
                .name("Name")
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    void shouldReturnConstraintWhenNameEmpty() {
        Film film = Film.builder()
                .id(1L)
                .description("Description")
                .duration(100)
                .releaseDate(LocalDate.of(1990, 7, 27))
                .name("")
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Name should not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void shouldReturnConstraintWhenDurationMinus1() {
        Film film = Film.builder()
                .id(1L)
                .description("Description")
                .duration(-1)
                .releaseDate(LocalDate.of(1990, 7, 27))
                .name("Name")
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Duration should be positive", violations.iterator().next().getMessage());
    }

    @Test
    void shouldReturnConstraintWhenDescriptionTooLong() {
        Film film = Film.builder()
                .id(1L)
                .description("Very very very very very very very very very very very very very very very very very" +
                        " very very very very very very very very very very very very very very very very " +
                        "very very very very very very very very very very very very very very very very " +
                        "very very very very very very very very very very very very very very very long description")
                .duration(100)
                .releaseDate(LocalDate.of(1990, 7, 27))
                .name("Name")
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Description should be shorter than 200 characters",
                violations.iterator().next().getMessage());
    }

    @Test
    void shouldReturnConstraintWhenReleaseDateBefore1895_12_28() {
        Film film = Film.builder()
                .id(1L)
                .description("Description")
                .duration(100)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .name("Name")
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Release date should be after 1895-12-28",
                violations.iterator().next().getMessage());
    }
}

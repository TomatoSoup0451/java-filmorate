package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
class UserValidationTest {

    private final Validator validator;

    @Autowired
    public UserValidationTest(Validator validator) {
        this.validator = validator;
    }

    @Test
    void shouldAcceptValidUser() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1990, 7, 27))
                .email("idm101@gmail.com")
                .name("Denis")
                .login("denis")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(0, violations.size());
    }

    @Test
    void shouldReturnConstraintWhenLoginEmpty() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1990, 7, 27))
                .email("idm101@gmail.com")
                .name("Denis")
                .login("")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Login should not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void shouldReturnConstraintWhenLoginHasWhiteSpace() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1990, 7, 27))
                .email("idm101@gmail.com")
                .name("Denis")
                .login("de nis")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Login should not contain whitespaces", violations.iterator().next().getMessage());
    }

    @Test
    void shouldReturnConstraintWhenEmailIsInvalid() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1990, 7, 27))
                .email("idm101gmail.com@")
                .name("Denis")
                .login("denis")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void shouldReturnConstraintWhenBirthdayIsInTheFuture() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(3000, 7, 27))
                .email("idm101@gmail.com")
                .name("Denis")
                .login("denis")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("Birthday should be in the past", violations.iterator().next().getMessage());
    }
}

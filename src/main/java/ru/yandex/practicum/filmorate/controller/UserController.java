package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        user.setId(++id);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User with id = " + user.getId() + " added");
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        if (!users.containsKey(user.getId())) {
            throw new IdNotFoundException("User with id = " + user.getId() + " not found");
        }
        users.put(user.getId(), user);
        log.info("User with id = " + user.getId() + " updated");
        return user;
    }

    @GetMapping("/users")
    public List<User> getUser() {
        return new ArrayList<>(users.values());
    }
}

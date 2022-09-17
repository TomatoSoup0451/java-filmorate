package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @PostMapping("/users")
    public User createUser(@Valid @RequestBody User user, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        return userStorage.createUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }
        return userStorage.updateUser(user);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable long id) {
        return userStorage.getUser(id);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable long id) {
        return userStorage.getUserFriends(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}

package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User createUser(User user) {
        user.setId(++id);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User with id = " + user.getId() + " added");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IdNotFoundException("User with id = " + user.getId() + " not found");
        }
        users.put(user.getId(), user);
        log.info("User with id = " + user.getId() + " updated");
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(long id) {
        if (!users.containsKey(id)) {
            throw new IdNotFoundException("User with id = " + id + " not found");
        }
        return users.get(id);
    }

    @Override
    public List<User> getMultipleUsers(long... id) {
        List<User> result = new ArrayList<>();
        for (long i : id) {
            if (!users.containsKey(i)) {
                throw new IdNotFoundException("User with id = " + i + " not found");
            }
            result.add(users.get(i));
        }
        return result;
    }

    @Override
    public List<User> getUserFriends(long id) {
        if (!users.containsKey(id)) {
            throw new IdNotFoundException("User with id = " + id + " not found");
        }
        List<User> result = new ArrayList<>();
        for (long l : users.get(id).getFriends()) {
            result.add(users.get(l));
        }
        return result;
    }
}

package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUser(long id);

    List<User> getUserFriends(long id);

    void updateFriendship(User user1, User user2, boolean isAdded);
}

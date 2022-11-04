package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    private UserService(@Qualifier("dbUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long id, long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        userStorage.updateFriendship(user, friend, true);
        log.info("User with id " + id + " added user with id " + friendId + " to friends");
    }

    public void removeFriend(long id, long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        userStorage.updateFriendship(user, friend, false);
        log.info("User with id " + id + " removed user with id " + friendId + " from friends");
    }

    public List<User> getCommonFriends(long id, long friendId) {
        User user1 = userStorage.getUser(id);
        User user2 = userStorage.getUser(friendId);
        Set<Long> common = new HashSet<>(user1.getFriends().keySet());
        common.retainAll(user2.getFriends().keySet());
        List<User> result = new ArrayList<>();
        for (long l : common) {
            result.add(userStorage.getUser(l));
        }
        return result;
    }
}


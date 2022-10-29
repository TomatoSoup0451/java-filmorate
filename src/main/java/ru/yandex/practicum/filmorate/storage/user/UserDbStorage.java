package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("dbUserStorage")
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        String sqlQueryAddUser = "insert into users(email, login, name, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQueryAddUser, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        long generatedId = keyHolder.getKey().longValue();
        user.setId(generatedId);
        log.info("User with id = " + user.getId() + " added");
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQueryUpdateUser = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        int result = jdbcTemplate.update(sqlQueryUpdateUser, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());
        if (result == 0)
            throw new IdNotFoundException("User with id = " + user.getId() + " not found");
        return user;
    }


    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "select id, email, login, name, birthday from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getUser(long id) {
        String sqlQuery = "select id, email, login, name, birthday " +
                "from users where id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException("User with id = " + id + " not found");
        }
    }

    @Override
    public List<User> getUserFriends(long id) {
        String sqlQuery = "select id, email, login, name, birthday " +
                "from users where id in (select friend_id from friends where user_id = ?)";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new IdNotFoundException("User with id = " + id + " not found");
        }
    }

    @Override
    public void updateFriendship(User user, User friend, boolean isAdded) {
        if (isAdded) {
            //user1.addFriend(user2);
            if (user.getFriends().containsKey(friend.getId())) {
                return;
            }
            user.addFriend(friend);
            boolean status = user.getFriends().get(friend.getId());
            jdbcTemplate.update("insert into friends(user_id, friend_id, friendship_status) values (?, ?, ?)",
                    user.getId(), friend.getId(), status);
            if (status) {
                jdbcTemplate.update("update friends set friendship_status = ? where user_id = ? and friend_id = ?",
                        status, friend.getId(), user.getId());
            }
        } else {
            if (!user.getFriends().containsKey(friend.getId())) {
                return;
            }
            user.removeFriend(friend);
            jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?",
                    user.getId(), friend.getId());
            if (friend.getFriends().containsKey(user.getId())) {
                jdbcTemplate.update("update friends set friendship_status = ? where user_id = ? and friend_id = ?",
                        false, friend.getId(), user.getId());
            }
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User result = User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet("select * from friends where user_id = ?",
                result.getId());
        while (friendsRows.next()) {
            result.addFriend(friendsRows.getLong("friend_id"),
                    friendsRows.getBoolean("friendship_status"));
        }
        return result;
    }

}

package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class User {

    @Email(message = "Email should be valid")
    private final String email;

    @NotBlank(message = "Login should not be blank")
    @Pattern(regexp = "^\\S*$", message = "Login should not contain whitespaces")
    private final String login;

    @Past(message = "Birthday should be in the past")
    private final LocalDate birthday;
    //Contains id of all friends. If boolean == true - friendship mutual
    private final Map<Long, Boolean> friends = new HashMap<>();
    private long id;
    private String name;

    /*
    If new friend already have id of current object in friends -> friendship mutual
     */
    public void addFriend(User user) {
        if (user.getFriends().containsKey(this.getId())) {
            friends.put(user.getId(), true);
        } else {
            friends.put(user.getId(), false);
        }
    }

    public void addFriend(Long id, Boolean isMutual) {
        friends.put(id, isMutual);
    }

    public void removeFriend(User user) {
        if (friends.containsKey(user.getId())) {
            friends.remove(user.getId());
            if (user.getFriends().containsKey(this.getId())) {
                user.getFriends().put(this.getId(), false);
            }
        } else {
            throw new IdNotFoundException("User with id=" + user.getId() + " not found in user" +
                    " with id=" + this.getId() + " friends list");
        }
    }

}

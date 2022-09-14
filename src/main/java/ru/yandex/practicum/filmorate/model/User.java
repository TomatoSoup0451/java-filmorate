package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

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
    private long id;
    private String name;
}

package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.ReleaseDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    @NotBlank(message = "Name should not be blank")
    private final String name;
    @Size(max = 200, message = "Description should be shorter than 200 characters")
    private final String description;
    @ReleaseDateValidation
    private final LocalDate releaseDate;
    @Positive(message = "Duration should be positive")
    private final int duration;
    private final Set<Long> likes = new HashSet<>();
    private long id;

    public void addLike(User user) {
        likes.add(user.getId());
    }

    public void removeLike(User user) {
        likes.remove(user.getId());
    }
}

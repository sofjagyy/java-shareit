package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> allUsers();

    Optional<User> user(Long id);

    void delete(User user);

    User save(User user);
}

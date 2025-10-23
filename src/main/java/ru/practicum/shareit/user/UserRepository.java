package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    void delete(User user);

    List<User> allUsers();

    Optional<User> userById(Long id);
}
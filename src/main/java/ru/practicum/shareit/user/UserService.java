package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> allUsers();

    User getUser(Long id);

    Optional<User> user(Long id);

    User updateUser(Long userId, User userData);

    void deleteUser(Long userId);

    void delete(User user);

    User save(User user);
}

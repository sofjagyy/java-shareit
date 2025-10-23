package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Component("InMemoryUserRepository")
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            if (usersContainsEmail(user.getEmail(), null)) {
                throw new DuplicateException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            user.setId(currentId.getAndIncrement());
            users.put(user.getId(), user);
        } else {
            User existingUser = users.get(user.getId());
            if (existingUser == null) {
                throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
            }
            if (usersContainsEmail(user.getEmail(), user.getId())) {
                throw new DuplicateException("Пользователь с email " + user.getEmail() + " уже существует");
            }
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId(), user);
    }

    @Override
    public List<User> allUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> userById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private boolean usersContainsEmail(String email, Long excludeUserId) {
        return users.values().stream()
                .filter(user -> excludeUserId == null || !user.getId().equals(excludeUserId))
                .anyMatch(user -> user.getEmail().equals(email));
    }
}
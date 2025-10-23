package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> allUsers() {
        return userRepository.allUsers();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.userById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public Optional<User> user(Long id) {
        return userRepository.userById(id);
    }

    @Override
    public User updateUser(Long userId, User userData) {
        User existingUser = getUser(userId);

        if (userData.getName() != null) {
            existingUser.setName(userData.getName());
        }
        if (userData.getEmail() != null) {
            existingUser.setEmail(userData.getEmail());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}


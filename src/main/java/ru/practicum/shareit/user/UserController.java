package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> allUsers() {
        return userService.allUsers().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto user(@PathVariable Long userId) {
        return UserMapper.toDto(userService.getUser(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Validated(UserDto.Create.class) @RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userService.save(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @Validated(UserDto.Update.class) @RequestBody UserDto userDto) {
        User userData = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userService.updateUser(userId, userData));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}


package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationGroups;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public Collection<UserDto> allUsers() {
        return userMapper.toDto(userService.allUsers());
    }

    @GetMapping("/{userId}")
    public UserDto user(@PathVariable Long userId) {
        return userMapper.toDto(userService.getUser(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Validated(ValidationGroups.Create.class) @RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        return userMapper.toDto(userService.save(user));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @Validated(ValidationGroups.Update.class) @RequestBody UserDto userDto) {
        User userData = userMapper.toEntity(userDto);
        return userMapper.toDto(userService.updateUser(userId, userData));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}


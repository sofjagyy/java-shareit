package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    User toEntity(UserDto dto);
}


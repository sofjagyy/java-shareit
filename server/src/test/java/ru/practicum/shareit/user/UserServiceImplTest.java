package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_whenValidUser_thenUserCreated() {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@example.com");

        UserDto createdUser = userService.createUser(userDto);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getName()).isEqualTo("John Doe");
        assertThat(createdUser.getEmail()).isEqualTo("john.doe@example.com");

        User userInDb = userRepository.findById(createdUser.getId()).orElse(null);
        assertThat(userInDb).isNotNull();
        assertThat(userInDb.getName()).isEqualTo("John Doe");
        assertThat(userInDb.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void createUser_whenEmailDuplicate_thenThrowConflictException() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("User 1");
        userDto1.setEmail("duplicate@example.com");
        userService.createUser(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("User 2");
        userDto2.setEmail("duplicate@example.com");

        assertThrows(ConflictException.class, () -> {
            userService.createUser(userDto2);
        });
    }

    @Test
    void updateUser_whenValidNameUpdate_thenUserUpdated() {
        UserDto userDto = new UserDto();
        userDto.setName("Original Name");
        userDto.setEmail("user@example.com");
        UserDto createdUser = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        UserDto updatedUser = userService.updateUser(createdUser.getId(), updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void updateUser_whenValidEmailUpdate_thenUserUpdated() {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("original@example.com");
        UserDto createdUser = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("updated@example.com");

        UserDto updatedUser = userService.updateUser(createdUser.getId(), updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void updateUser_whenEmailDuplicate_thenThrowConflictException() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("User 1");
        userDto1.setEmail("user1@example.com");
        userService.createUser(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("User 2");
        userDto2.setEmail("user2@example.com");
        UserDto createdUser2 = userService.createUser(userDto2);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("user1@example.com");

        assertThrows(ConflictException.class, () -> {
            userService.updateUser(createdUser2.getId(), updateDto);
        });
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowNotFoundException() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(999L, updateDto);
        });
    }

    @Test
    void getUserById_whenUserExists_thenReturnUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        UserDto createdUser = userService.createUser(userDto);

        UserDto retrievedUser = userService.getUserById(createdUser.getId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isEqualTo(createdUser.getId());
        assertThat(retrievedUser.getName()).isEqualTo("Test User");
        assertThat(retrievedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUserById_whenUserNotFound_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void getAllUsers_whenUsersExist_thenReturnAllUsers() {
        UserDto userDto1 = new UserDto();
        userDto1.setName("User 1");
        userDto1.setEmail("user1@example.com");
        userService.createUser(userDto1);

        UserDto userDto2 = new UserDto();
        userDto2.setName("User 2");
        userDto2.setEmail("user2@example.com");
        userService.createUser(userDto2);

        List<UserDto> users = userService.getAllUsers();

        assertThat(users).isNotNull();
        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void deleteUser_whenUserExists_thenUserDeleted() {
        UserDto userDto = new UserDto();
        userDto.setName("User to Delete");
        userDto.setEmail("delete@example.com");
        UserDto createdUser = userService.createUser(userDto);

        userService.deleteUser(createdUser.getId());

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(createdUser.getId());
        });
    }

    @Test
    void deleteUser_whenUserNotFound_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
    }

    @Test
    void updateUser_whenBothNameAndEmailUpdate_thenUserUpdated() {
        UserDto userDto = new UserDto();
        userDto.setName("Original Name");
        userDto.setEmail("original@example.com");
        UserDto createdUser = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("new@example.com");

        UserDto updatedUser = userService.updateUser(createdUser.getId(), updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("New Name");
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void updateUser_whenSameEmailUpdate_thenUserUpdated() {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("user@example.com");
        UserDto createdUser = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("user@example.com");

        UserDto updatedUser = userService.updateUser(createdUser.getId(), updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void getAllUsers_whenNoUsers_thenReturnEmptyList() {
        List<UserDto> users = userService.getAllUsers();

        assertThat(users).isNotNull();
    }
}


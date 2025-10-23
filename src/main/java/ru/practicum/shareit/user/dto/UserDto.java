package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.ValidationGroups;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = ValidationGroups.Create.class, message = "Имя не может быть пустым")
    private String name;
    @NotBlank(groups = ValidationGroups.Create.class, message = "Email не может быть пустым")
    @Email(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class}, message = "Email должен быть корректным")
    private String email;
}


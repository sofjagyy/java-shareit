package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.ValidationGroups;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = ValidationGroups.Create.class, message = "Название не может быть пустым")
    private String name;
    @NotBlank(groups = ValidationGroups.Create.class, message = "Описание не может быть пустым")
    private String description;
    @NotNull(groups = ValidationGroups.Create.class, message = "Доступность не может быть null")
    private Boolean available;
    private Long requestId;
}

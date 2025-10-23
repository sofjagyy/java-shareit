package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Long id;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Запрашивающий не может быть null")
    private User requestor;
    private LocalDateTime created;
}
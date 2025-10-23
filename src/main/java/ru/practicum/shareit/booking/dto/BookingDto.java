package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.validation.ValidationGroups;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(groups = ValidationGroups.Create.class, message = "Дата начала не может быть null")
    @FutureOrPresent(groups = ValidationGroups.Create.class, message = "Дата начала должна быть в настоящем или будущем")
    private LocalDateTime start;
    @NotNull(groups = ValidationGroups.Create.class, message = "Дата окончания не может быть null")
    @Future(groups = ValidationGroups.Create.class, message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;
    @NotNull(groups = ValidationGroups.Create.class, message = "ID вещи не может быть null")
    private Long itemId;
    private UserShortDto booker;
    private ItemShortDto item;
    private Status status;
}

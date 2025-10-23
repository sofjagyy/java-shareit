package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(groups = Create.class, message = "Дата начала не может быть null")
    @FutureOrPresent(groups = Create.class, message = "Дата начала должна быть в настоящем или будущем")
    private LocalDateTime start;
    @NotNull(groups = Create.class, message = "Дата окончания не может быть null")
    @Future(groups = Create.class, message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;
    @NotNull(groups = Create.class, message = "ID вещи не может быть null")
    private Long itemId;
    private BookerDto booker;
    private ItemDto item;
    private Status status;

    public interface Create {}

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookerDto {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ItemDto {
        private Long id;
        private String name;
    }
}

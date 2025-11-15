package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 12, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 2, 10, 0, 0);

        ItemDto itemDto = new ItemDto(1L, "Drill", "Power drill", true, null);
        UserDto userDto = new UserDto(2L, "John Doe", "john@example.com");

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);
        bookingDto.setItemId(1L);
        bookingDto.setItem(itemDto);
        bookingDto.setCreator(userDto);
        bookingDto.setStatus(BookingStatus.APPROVED);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");

        assertThat(result).doesNotHaveJsonPath("$.creator");
        assertThat(result).doesNotHaveJsonPath("$.startDate");
        assertThat(result).doesNotHaveJsonPath("$.endDate");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = """
                {
                    "id": 1,
                    "start": "2024-12-01T10:00:00",
                    "end": "2024-12-02T10:00:00",
                    "itemId": 1,
                    "item": {
                        "id": 1,
                        "name": "Drill",
                        "description": "Power drill",
                        "available": true
                    },
                    "booker": {
                        "id": 2,
                        "name": "John Doe",
                        "email": "john@example.com"
                    },
                    "status": "APPROVED"
                }
                """;

        BookingDto result = json.parse(content).getObject();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStartDate()).isEqualTo(LocalDateTime.of(2024, 12, 1, 10, 0, 0));
        assertThat(result.getEndDate()).isEqualTo(LocalDateTime.of(2024, 12, 2, 10, 0, 0));
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(1L);
        assertThat(result.getItem().getName()).isEqualTo("Drill");
        assertThat(result.getCreator()).isNotNull();
        assertThat(result.getCreator().getId()).isEqualTo(2L);
        assertThat(result.getCreator().getName()).isEqualTo("John Doe");
        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testDateTimeFormat() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 12, 1, 10, 30, 45);
        LocalDateTime end = LocalDateTime.of(2024, 12, 2, 15, 45, 30);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);
        bookingDto.setStatus(BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
    }
}


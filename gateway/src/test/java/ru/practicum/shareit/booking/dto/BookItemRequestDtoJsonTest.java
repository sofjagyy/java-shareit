package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 12, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 2, 10, 0, 0);

        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);

        JsonContent<BookItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{"
                + "\"itemId\": 1,"
                + "\"start\": \"2024-12-01T10:00:00\","
                + "\"end\": \"2024-12-02T10:00:00\""
                + "}";

        BookItemRequestDto result = json.parse(content).getObject();

        assertThat(result).isNotNull();
        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 12, 1, 10, 0, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 12, 2, 10, 0, 0));
    }

    @Test
    void testDateTimeFormat() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 12, 15, 14, 30, 45);
        LocalDateTime end = LocalDateTime.of(2024, 12, 20, 16, 45, 30);

        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);

        JsonContent<BookItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
        assertThat(result).extractingJsonPathStringValue("$.start")
                .contains("2024-12-15T14:30:45");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .contains("2024-12-20T16:45:30");
    }
}


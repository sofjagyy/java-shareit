package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingShortDtoJsonTest {

    @Autowired
    private JacksonTester<BookingShortDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingShortDto bookingShortDto = new BookingShortDto(1L, 2L);

        JsonContent<BookingShortDto> result = json.write(bookingShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);

        assertThat(result).doesNotHaveJsonPath("$.creatorId");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = """
                {
                    "id": 1,
                    "bookerId": 2
                }
                """;

        BookingShortDto result = json.parse(content).getObject();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCreatorId()).isEqualTo(2L);
    }

    @Test
    void testSerializeWithNullValues() throws Exception {
        BookingShortDto bookingShortDto = new BookingShortDto(null, null);

        JsonContent<BookingShortDto> result = json.write(bookingShortDto);

        assertThat(result).extractingJsonPathValue("$.id").isNull();
        assertThat(result).extractingJsonPathValue("$.bookerId").isNull();
    }
}


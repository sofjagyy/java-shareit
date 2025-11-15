package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 12, 1, 10, 30, 45);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a drill");
        requestDto.setCreated(created);
        requestDto.setItems(Arrays.asList(
                new ItemDto(1L, "Electric Drill", "Power drill", true, 1L)
        ));

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotNull();
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Electric Drill");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{"
                + "\"id\": 1,"
                + "\"description\": \"Need a drill\","
                + "\"created\": \"2024-12-01T10:30:45\","
                + "\"items\": [{"
                + "\"id\": 1,"
                + "\"name\": \"Electric Drill\","
                + "\"description\": \"Power drill\","
                + "\"available\": true,"
                + "\"requestId\": 1"
                + "}]"
                + "}";

        ItemRequestDto result = json.parse(content).getObject();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Need a drill");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2024, 12, 1, 10, 30, 45));
        assertThat(result.getItems()).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Electric Drill");
    }

    @Test
    void testSerializeWithEmptyItems() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 12, 1, 10, 30, 45);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(2L);
        requestDto.setDescription("Need a hammer");
        requestDto.setCreated(created);
        requestDto.setItems(Arrays.asList());

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a hammer");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(0);
    }

    @Test
    void testDateTimeFormat() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 12, 15, 14, 25, 30);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Test");
        requestDto.setCreated(created);

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathStringValue("$.created")
                .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .contains("2024-12-15T14:25:30");
    }
}


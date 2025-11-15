package ru.practicum.shareit.item.comments;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 12, 1, 10, 30, 45);

        CommentDto commentDto = new CommentDto(
                1L,
                "Great item!",
                "John Doe",
                created
        );

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotNull();

        assertThat(result).doesNotHaveJsonPath("$.creatorName");
        assertThat(result).doesNotHaveJsonPath("$.createdAt");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{"
                + "\"id\": 1,"
                + "\"text\": \"Great item!\","
                + "\"authorName\": \"John Doe\","
                + "\"created\": \"2024-12-01T10:30:45\""
                + "}";

        CommentDto result = json.parse(content).getObject();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Great item!");
        assertThat(result.getCreatorName()).isEqualTo("John Doe");
        assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 12, 1, 10, 30, 45));
    }

    @Test
    void testDateTimeFormat() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 12, 15, 14, 25, 30);

        CommentDto commentDto = new CommentDto(
                1L,
                "Excellent!",
                "Jane Smith",
                created
        );

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathStringValue("$.created")
                .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .contains("2024-12-15T14:25:30");
    }
}


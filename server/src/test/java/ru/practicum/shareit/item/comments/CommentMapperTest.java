package ru.practicum.shareit.item.comments;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    void toDto_whenValidComment_thenReturnDto() {
        User creator = new User();
        creator.setId(1L);
        creator.setName("Creator");
        creator.setEmail("creator@example.com");

        Item item = new Item();
        item.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setCreator(creator);
        comment.setCreatedAt(LocalDateTime.now());

        CommentDto dto = commentMapper.toDto(comment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Great item!");
        assertThat(dto.getCreatorName()).isEqualTo("Creator");
    }

    @Test
    void toEntity_whenValidDto_thenReturnEntity() {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Great item!");
        dto.setCreatorName("Creator");
        dto.setCreatedAt(LocalDateTime.now());

        Comment comment = commentMapper.toEntity(dto);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo("Great item!");
    }

    @Test
    void toDto_whenListOfComments_thenReturnListOfDtos() {
        User creator = new User();
        creator.setId(1L);
        creator.setName("Creator");
        creator.setEmail("creator@example.com");

        Item item = new Item();
        item.setId(1L);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setText("Comment 1");
        comment1.setItem(item);
        comment1.setCreator(creator);
        comment1.setCreatedAt(LocalDateTime.now());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Comment 2");
        comment2.setItem(item);
        comment2.setCreator(creator);
        comment2.setCreatedAt(LocalDateTime.now());

        List<CommentDto> dtos = commentMapper.toDto(Arrays.asList(comment1, comment2));

        assertThat(dtos).isNotNull();
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getText()).isEqualTo("Comment 1");
        assertThat(dtos.get(1).getText()).isEqualTo("Comment 2");
    }
}


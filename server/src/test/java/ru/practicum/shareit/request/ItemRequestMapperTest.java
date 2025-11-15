package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemRequestMapperTest {

    @Autowired
    private ItemRequestMapper itemRequestMapper;

    @Test
    void toDto_whenValidItemRequest_thenReturnDto() {
        User creator = new User();
        creator.setId(1L);
        creator.setName("Creator");
        creator.setEmail("creator@example.com");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setCreator(creator);
        request.setCreatedAt(LocalDateTime.now());

        ItemRequestDto dto = itemRequestMapper.toDto(request);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Need a drill");
        assertThat(dto.getCreated()).isNotNull();
    }

    @Test
    void toEntity_whenValidDto_thenReturnEntity() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Need a drill");
        dto.setCreated(LocalDateTime.now());

        ItemRequest request = itemRequestMapper.toEntity(dto);

        assertThat(request).isNotNull();
        assertThat(request.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void toDto_whenListOfRequests_thenReturnListOfDtos() {
        User creator = new User();
        creator.setId(1L);
        creator.setName("Creator");
        creator.setEmail("creator@example.com");

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("Need a drill");
        request1.setCreator(creator);
        request1.setCreatedAt(LocalDateTime.now());

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Need a saw");
        request2.setCreator(creator);
        request2.setCreatedAt(LocalDateTime.now());

        List<ItemRequestDto> dtos = itemRequestMapper.toDto(Arrays.asList(request1, request2));

        assertThat(dtos).isNotNull();
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getDescription()).isEqualTo("Need a drill");
        assertThat(dtos.get(1).getDescription()).isEqualTo("Need a saw");
    }
}


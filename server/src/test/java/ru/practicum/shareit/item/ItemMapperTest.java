package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    void toDto_whenValidItem_thenReturnDto() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemDto dto = itemMapper.toDto(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Item");
        assertThat(dto.getDescription()).isEqualTo("Description");
        assertThat(dto.getAvailable()).isTrue();
    }

    @Test
    void toEntity_whenValidDto_thenReturnEntity() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);

        Item item = itemMapper.toEntity(dto);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Item");
        assertThat(item.getDescription()).isEqualTo("Description");
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    void toDto_whenListOfItems_thenReturnListOfDtos() {
        User owner = new User();
        owner.setId(1L);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);
        item2.setOwner(owner);

        List<ItemDto> dtos = itemMapper.toDto(Arrays.asList(item1, item2));

        assertThat(dtos).isNotNull();
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("Item 1");
        assertThat(dtos.get(1).getName()).isEqualTo("Item 2");
    }

    @Test
    void toDtoWithBookings_whenValidItem_thenReturnDtoWithBookings() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setComments(Collections.emptyList());

        ItemWithBookingsDto dto = itemMapper.toDtoWithBookings(item);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Item");
    }
}


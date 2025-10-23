package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    public ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public List<ItemDto> toDto(Collection<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Item toEntity(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }

    public Item toEntityWithOwner(ItemDto itemDto, User owner) {
        Item item = toEntity(itemDto);
        if (item != null) {
            item.setOwner(owner);
        }
        return item;
    }
}


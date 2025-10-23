package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    public ItemRequestDto toDto(ItemRequest itemRequest, List<ItemDto> items) {
        if (itemRequest == null) {
            return null;
        }
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }

    public List<ItemRequestDto> toDto(Collection<ItemRequest> itemRequests, List<ItemDto> items) {
        if (itemRequests == null) {
            return null;
        }
        return itemRequests.stream()
                .map(itemRequest -> toDto(itemRequest, items))
                .collect(Collectors.toList());
    }

    public ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            return null;
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }
}


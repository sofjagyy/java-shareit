package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemWithBookingsDto getItemById(Long userId, Long itemId);

    List<ItemWithBookingsDto> getItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);
    
    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}


package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> allItems();

    Item getItem(Long id);

    Optional<Item> item(Long id);

    Item createItem(Long userId, ItemDto itemDto);

    Item updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long userId, Long itemId);

    void delete(Item item);

    Item save(Item item);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> searchByText(String text);

    Comment addComment(Long userId, Long itemId, CommentDto commentDto);
}


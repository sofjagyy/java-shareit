package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> allItems();

    Optional<Item> item(Long id);

    void delete(Item item);

    Item save(Item item);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> searchByText(String text);

    Comment addComment(Comment comment, Long userId);

    List<Comment> getCommentsByItemId(Long itemId);
}


package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    void delete(Item item);

    List<Item> allItems();

    Optional<Item> itemById(Long id);

    List<Item> findByOwnerId(Long ownerId);

    List<Item> searchByText(String text);
}


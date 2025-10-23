package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(currentId.getAndIncrement());
            items.put(item.getId(), item);
        } else {
            Item existingItem = items.get(item.getId());
            if (existingItem == null) {
                throw new NotFoundException("Вещь с id " + item.getId() + " не найдена");
            }
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public void delete(Item item) {
        items.remove(item.getId());
    }

    @Override
    public List<Item> allItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> itemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(lowerText) ||
                        item.getDescription().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
    }
}


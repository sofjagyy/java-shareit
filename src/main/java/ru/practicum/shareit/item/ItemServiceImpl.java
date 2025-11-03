package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = getUserById(userId);

        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        item = itemRepository.save(item);

        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = getItemByIdInternal(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является владельцем");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toDto(itemRepository.update(item));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemMapper.toDto(getItemByIdInternal(itemId));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        return itemMapper.toDto(itemRepository.findAllByOwnerId(userId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemMapper.toDto(itemRepository.searchByText(text));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item getItemByIdInternal(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }
}


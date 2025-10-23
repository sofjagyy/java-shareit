package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Override
    public List<Item> allItems() {
        return itemRepository.allItems();
    }

    @Override
    public Item getItem(Long id) {
        return itemRepository.itemById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    @Override
    public Optional<Item> item(Long id) {
        return itemRepository.itemById(id);
    }

    @Override
    public Item createItem(Long userId, ItemDto itemDto) {
        User owner = userService.getUser(userId);
        Item item = ItemMapper.toItemWithOwner(itemDto, owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestService.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос вещи не найден"));
            item.setRequest(itemRequest);
        }

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = getItem(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является владельцем вещи");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return itemRepository.save(existingItem);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        Item item = getItem(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является владельцем вещи");
        }

        itemRepository.delete(item);
    }

    @Override
    public void delete(Item item) {
        itemRepository.delete(item);
    }

    @Override
    public Item save(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public List<Item> findByOwnerId(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Item> searchByText(String text) {
        return itemRepository.searchByText(text);
    }

    @Override
    public Comment addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = getItem(itemId);
        User author = userService.getUser(userId);

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);

        return addComment(comment, userId);
    }

    @Override
    public Comment addComment(Comment comment, Long userId) {
        List<Booking> bookings = bookingRepository.findByBookerId(userId);
        LocalDateTime now = LocalDateTime.now();

        boolean hasFinishedBooking = bookings.stream()
                .anyMatch(b -> b.getItem().getId().equals(comment.getItem().getId()) &&
                        b.getEnd().isBefore(now) &&
                        b.getStatus() == Status.APPROVED);

        if (!hasFinishedBooking) {
            throw new IllegalArgumentException("Вы можете оставлять комментарии только к вещам, которые брали в аренду");
        }

        comment.setCreated(now);
        return itemRepository.saveComment(comment);
    }

    @Override
    public List<Comment> getCommentsByItemId(Long itemId) {
        return itemRepository.findCommentsByItemId(itemId);
    }
}



package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User owner = getUserById(userId);

        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        item = itemRepository.save(item);

        return itemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = getItem(itemId);

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

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemWithBookingsDto getItemById(Long userId, Long itemId) {
        Item item = getItem(itemId);

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            lastBooking = bookingRepository.findLastBooking(itemId, now).orElse(null);
            nextBooking = bookingRepository.findNextBooking(itemId, now).orElse(null);
        }

        return buildItemWithBookings(item, lastBooking, nextBooking);
    }

    @Override
    public List<ItemWithBookingsDto> getItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (items.isEmpty()) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Booking> lastBookings = bookingRepository.findLastBookingsForItems(itemIds, now);
        Map<Long, Booking> lastBookingByItemId = lastBookings.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (existing, replacement) -> existing
                ));

        List<Booking> nextBookings = bookingRepository.findNextBookingsForItems(itemIds, now);
        Map<Long, Booking> nextBookingByItemId = nextBookings.stream()
                .collect(Collectors.toMap(
                        booking -> booking.getItem().getId(),
                        booking -> booking,
                        (existing, replacement) -> existing
                ));

        return items.stream()
                .map(item -> buildItemWithBookings(item, lastBookingByItemId, nextBookingByItemId))
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemMapper.toDto(itemRepository.search(text));
    }

    private ItemWithBookingsDto buildItemWithBookings(Item item, Booking lastBooking, Booking nextBooking) {
        ItemWithBookingsDto result = itemMapper.toDtoWithBookings(item);
        result.setLastBooking(bookingMapper.toShortDto(lastBooking));
        result.setNextBooking(bookingMapper.toShortDto(nextBooking));
        result.setComments(commentMapper.toDto(item.getComments()));
        return result;
    }

    private ItemWithBookingsDto buildItemWithBookings(
            Item item,
            Map<Long, Booking> lastBookingByItemId,
            Map<Long, Booking> nextBookingByItemId) {
        Booking lastBooking = lastBookingByItemId.get(item.getId());
        Booking nextBooking = nextBookingByItemId.get(item.getId());
        return buildItemWithBookings(item, lastBooking, nextBooking);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }
}
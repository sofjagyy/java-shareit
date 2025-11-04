package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.comments.CommentRepository;
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
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
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
            List<Booking> lastBookings = bookingRepository.findLastBooking(itemId, now);
            List<Booking> nextBookings = bookingRepository.findNextBooking(itemId, now);

            lastBooking = lastBookings.isEmpty() ? null : lastBookings.get(0);
            nextBooking = nextBookings.isEmpty() ? null : nextBookings.get(0);
        }

        List<Comment> comments = commentRepository.findByItemId(itemId);

        return itemMapper.toDtoWithBookings(item, lastBooking, nextBooking, comments);
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

        List<Comment> allComments = commentRepository.findByItemIdIn(itemIds);
        Map<Long, List<Comment>> commentsByItemId = allComments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

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
                .map(item -> {
                    Booking lastBooking = lastBookingByItemId.get(item.getId());
                    Booking nextBooking = nextBookingByItemId.get(item.getId());
                    List<Comment> comments = commentsByItemId.getOrDefault(item.getId(), List.of());

                    return itemMapper.toDtoWithBookings(item, lastBooking, nextBooking, comments);
                })
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemMapper.toDto(itemRepository.search(text));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = getUserById(userId);
        Item item = getItem(itemId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> completedBookings = bookingRepository.findByCreatorIdAndItemIdAndStatusAndEndIsBefore(
                userId, itemId, BookingStatus.APPROVED, now
        );

        if (completedBookings.isEmpty()) {
            throw new IllegalArgumentException("Пользователь не брал вещь в аренду или аренда еще не завершена");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(now);

        comment = commentRepository.save(comment);

        return commentMapper.toDto(comment);
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
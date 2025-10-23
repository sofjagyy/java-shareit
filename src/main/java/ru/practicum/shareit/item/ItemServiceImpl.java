package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<Item> allItems() {
        return itemRepository.allItems();
    }

    @Override
    public Optional<Item> item(Long id) {
        return itemRepository.itemById(id);
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



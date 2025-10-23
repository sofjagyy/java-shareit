package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public Booking create(Booking booking, Long bookerId, Long itemId) {
        Item item = itemService.item(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User booker = userService.user(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (item.getOwner().getId().equals(bookerId)) {
            throw new ForbiddenException("Нельзя забронировать свою собственную вещь");
        }

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }

        if (booking.getStart() == null || booking.getEnd() == null) {
            throw new IllegalArgumentException("Дата начала и окончания обязательны");
        }

        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().equals(booking.getStart())) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
        }

        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата начала должна быть в будущем");
        }

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Только владелец вещи может подтвердить бронирование");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new IllegalArgumentException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Доступ запрещен");
        }

        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId, BookingState state) {
        List<Booking> bookings = bookingRepository.findByBookerId(bookerId);
        return filterByState(bookings, state);
    }

    @Override
    public List<Booking> findByOwnerId(Long ownerId, BookingState state) {
        List<Booking> bookings = bookingRepository.findByOwnerId(ownerId);
        return filterByState(bookings, state);
    }

    private List<Booking> filterByState(List<Booking> bookings, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        if (state == null) {
            state = BookingState.ALL;
        }

        switch (state) {
            case CURRENT:
                return bookings.stream()
                        .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                        .collect(Collectors.toList());
            case PAST:
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .collect(Collectors.toList());
            case FUTURE:
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .collect(Collectors.toList());
            case WAITING:
                return bookings.stream()
                        .filter(b -> b.getStatus() == Status.WAITING)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookings.stream()
                        .filter(b -> b.getStatus() == Status.REJECTED)
                        .collect(Collectors.toList());
            case ALL:
            default:
                return bookings;
        }
    }
}


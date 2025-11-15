package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        User creator = getUserById(userId);

        Item item = getItemById(bookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Владелец не может забронировать свою вещь");
        }

        Booking booking = bookingMapper.toEntity(bookingDto);
        booking.setItem(item);
        booking.setCreator(creator);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingByIdInternal(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Только владелец вещи может подтвердить бронирование");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalArgumentException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = getBookingByIdInternal(bookingId);

        if (!booking.getCreator().getId().equals(userId) &&
            !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Просмотр доступен только автору бронирования или владельцу вещи");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByCreator(Long userId, BookingState state) {
        getUserById(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByCreatorId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByCreatorIdAndStartDateIsBeforeAndEndDateIsAfter(userId, now, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findByCreatorIdAndEndDateIsBefore(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByCreatorIdAndStartDateIsAfter(userId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByCreatorIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByCreatorIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }

        return bookingMapper.toDto(bookings);
    }

    @Override
    public List<BookingDto> getBookingsByItemOwner(Long userId, BookingState state) {
        getUserById(userId);

        Sort sort = Sort.by(Sort.Direction.DESC, "startDate");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerId(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(userId, now, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndDateIsBefore(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartDateIsAfter(userId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }

        return bookingMapper.toDto(bookings);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    private Booking getBookingByIdInternal(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
    }
}
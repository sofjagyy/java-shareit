package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Владелец не может забронировать свою вещь");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

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
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
            !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Просмотр доступен только автору бронирования или владельцу вещи");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        return getBookings(userId, state, false);
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long userId, BookingState state) {
        return getBookings(userId, state, true);
    }

    private List<BookingDto> getBookings(Long userId, BookingState state, boolean isOwner) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = isOwner
                        ? bookingRepository.findByItemOwnerId(userId, sort)
                        : bookingRepository.findByBookerId(userId, sort);
                break;
            case CURRENT:
                bookings = isOwner
                        ? bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort)
                        : bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, now, now, sort);
                break;
            case PAST:
                bookings = isOwner
                        ? bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, now, sort)
                        : bookingRepository.findByBookerIdAndEndIsBefore(userId, now, sort);
                break;
            case FUTURE:
                bookings = isOwner
                        ? bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, now, sort)
                        : bookingRepository.findByBookerIdAndStartIsAfter(userId, now, sort);
                break;
            case WAITING:
                bookings = isOwner
                        ? bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort)
                        : bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
                break;
            case REJECTED:
                bookings = isOwner
                        ? bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort)
                        : bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
                break;
            default:
                throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }

        return bookingMapper.toDto(bookings);
    }
}


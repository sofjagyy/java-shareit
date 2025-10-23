package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking create(Booking booking, Long bookerId, Long itemId);

    Booking approve(Long bookingId, Long userId, Boolean approved);

    Booking getBooking(Long bookingId, Long userId);

    Optional<Booking> findById(Long id);

    List<Booking> findByBookerId(Long bookerId, BookingState state);

    List<Booking> findByOwnerId(Long ownerId, BookingState state);
}


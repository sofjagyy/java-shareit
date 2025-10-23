package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking create(Booking booking, Long bookerId, Long itemId);

    Booking approve(Long bookingId, Long userId, Boolean approved);

    Optional<Booking> findById(Long id);

    List<Booking> findByBookerId(Long bookerId, String state);

    List<Booking> findByOwnerId(Long ownerId, String state);
}


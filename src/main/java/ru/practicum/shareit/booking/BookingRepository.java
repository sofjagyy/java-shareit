package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByOwnerId(Long ownerId);
}


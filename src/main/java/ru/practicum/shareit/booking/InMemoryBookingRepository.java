package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Component("InMemoryBookingRepository")
public class InMemoryBookingRepository implements BookingRepository {
    private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(currentId.getAndIncrement());
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker() != null && booking.getBooker().getId().equals(bookerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByOwnerId(Long ownerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getItem() != null && 
                        booking.getItem().getOwner() != null && 
                        booking.getItem().getOwner().getId().equals(ownerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }
}


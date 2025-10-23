package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        if (booking.getBooker() != null) {
            dto.setBooker(new UserShortDto(
                    booking.getBooker().getId(),
                    booking.getBooker().getName()
            ));
        }

        if (booking.getItem() != null) {
            dto.setItem(new ItemShortDto(
                    booking.getItem().getId(),
                    booking.getItem().getName()
            ));
        }

        return dto;
    }

    public List<BookingDto> toDto(Collection<Booking> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Booking toEntity(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}


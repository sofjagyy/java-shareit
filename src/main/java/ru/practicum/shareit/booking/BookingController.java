package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.validation.ValidationGroups;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Validated(ValidationGroups.Create.class) @RequestBody BookingDto bookingDto) {
        Booking booking = bookingMapper.toEntity(bookingDto);
        Booking created = bookingService.create(booking, userId, bookingDto.getItemId());
        return bookingMapper.toDto(created);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long bookingId,
                                      @RequestParam Boolean approved) {
        Booking booking = bookingService.approve(bookingId, userId, approved);
        return bookingMapper.toDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long bookingId) {
        Booking booking = bookingService.getBooking(bookingId, userId);
        return bookingMapper.toDto(booking);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingMapper.toDto(bookingService.findByBookerId(userId, state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingMapper.toDto(bookingService.findByOwnerId(userId, state));
    }
}


package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BookingMapperTest {

    @Autowired
    private BookingMapper bookingMapper;

    @Test
    void toDto_whenValidBooking_thenReturnDto() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setCreator(user);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = bookingMapper.toDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void toEntity_whenValidDto_thenReturnEntity() {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStartDate(LocalDateTime.now());
        dto.setEndDate(LocalDateTime.now().plusDays(1));
        dto.setItemId(1L);

        Booking booking = bookingMapper.toEntity(dto);

        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(1L);
    }

    @Test
    void toDto_whenListOfBookings_thenReturnListOfDtos() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStartDate(LocalDateTime.now());
        booking1.setEndDate(LocalDateTime.now().plusDays(1));
        booking1.setItem(item);
        booking1.setCreator(user);
        booking1.setStatus(BookingStatus.WAITING);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStartDate(LocalDateTime.now().plusDays(2));
        booking2.setEndDate(LocalDateTime.now().plusDays(3));
        booking2.setItem(item);
        booking2.setCreator(user);
        booking2.setStatus(BookingStatus.APPROVED);

        List<BookingDto> dtos = bookingMapper.toDto(Arrays.asList(booking1, booking2));

        assertThat(dtos).isNotNull();
        assertThat(dtos).hasSize(2);
    }

    @Test
    void toShortDto_whenNullBooking_thenReturnNull() {
        BookingShortDto dto = bookingMapper.toShortDto(null);

        assertThat(dto).isNull();
    }

    @Test
    void toShortDto_whenValidBooking_thenReturnShortDto() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now());
        booking.setEndDate(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setCreator(user);
        booking.setStatus(BookingStatus.WAITING);

        BookingShortDto dto = bookingMapper.toShortDto(booking);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
    }
}


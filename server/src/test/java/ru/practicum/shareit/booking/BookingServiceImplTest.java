package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void createBooking_whenValidBooking_thenBookingCreated() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getStartDate()).isEqualTo(start);
        assertThat(createdBooking.getEndDate()).isEqualTo(end);
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(createdBooking.getItem()).isNotNull();
        assertThat(createdBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking.getCreator()).isNotNull();
        assertThat(createdBooking.getCreator().getId()).isEqualTo(booker.getId());

        Booking bookingInDb = bookingRepository.findById(createdBooking.getId()).orElse(null);
        assertThat(bookingInDb).isNotNull();
        assertThat(bookingInDb.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookingInDb.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingInDb.getCreator().getId()).isEqualTo(booker.getId());
    }
}


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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;

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

    @Test
    void createBooking_whenItemNotAvailable_thenThrowIllegalArgumentException() {
        item.setAvailable(false);
        itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(booker.getId(), bookingDto);
        });
    }

    @Test
    void createBooking_whenOwnerTriesToBook_thenThrowForbiddenException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        assertThrows(ForbiddenException.class, () -> {
            bookingService.createBooking(owner.getId(), bookingDto);
        });
    }

    @Test
    void createBooking_whenUserNotFound_thenThrowNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(999L, bookingDto);
        });
    }

    @Test
    void createBooking_whenItemNotFound_thenThrowNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(999L);
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(booker.getId(), bookingDto);
        });
    }

    @Test
    void approveBooking_whenValidApproval_thenBookingApproved() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        BookingDto approvedBooking = bookingService.approveBooking(owner.getId(), createdBooking.getId(), true);

        assertThat(approvedBooking).isNotNull();
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_whenValidRejection_thenBookingRejected() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        BookingDto rejectedBooking = bookingService.approveBooking(owner.getId(), createdBooking.getId(), false);

        assertThat(rejectedBooking).isNotNull();
        assertThat(rejectedBooking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void approveBooking_whenNotOwner_thenThrowForbiddenException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        assertThrows(ForbiddenException.class, () -> {
            bookingService.approveBooking(booker.getId(), createdBooking.getId(), true);
        });
    }

    @Test
    void approveBooking_whenAlreadyProcessed_thenThrowIllegalArgumentException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.approveBooking(owner.getId(), createdBooking.getId(), true);

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.approveBooking(owner.getId(), createdBooking.getId(), true);
        });
    }

    @Test
    void getBookingById_whenCreatorRequests_thenReturnBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        BookingDto retrievedBooking = bookingService.getBookingById(booker.getId(), createdBooking.getId());

        assertThat(retrievedBooking).isNotNull();
        assertThat(retrievedBooking.getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void getBookingById_whenOwnerRequests_thenReturnBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        BookingDto retrievedBooking = bookingService.getBookingById(owner.getId(), createdBooking.getId());

        assertThat(retrievedBooking).isNotNull();
        assertThat(retrievedBooking.getId()).isEqualTo(createdBooking.getId());
    }

    @Test
    void getBookingById_whenOtherUserRequests_thenThrowForbiddenException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStartDate(start);
        bookingDto.setEndDate(end);

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        User otherUser = new User();
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");
        otherUser = userRepository.save(otherUser);

        Long otherUserId = otherUser.getId();

        assertThrows(ForbiddenException.class, () -> {
            bookingService.getBookingById(otherUserId, createdBooking.getId());
        });
    }

    @Test
    void getBookingsByCreator_withStateAll_thenReturnAllBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto pastDto = new BookingDto();
        pastDto.setItemId(item.getId());
        pastDto.setStartDate(now.minusDays(3));
        pastDto.setEndDate(now.minusDays(2));
        bookingService.createBooking(booker.getId(), pastDto);

        BookingDto futureDto = new BookingDto();
        futureDto.setItemId(item.getId());
        futureDto.setStartDate(now.plusDays(1));
        futureDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), futureDto);

        List<BookingDto> bookings = bookingService.getBookingsByCreator(booker.getId(), BookingState.ALL);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(2);
    }

    @Test
    void getBookingsByCreator_withStateCurrent_thenReturnCurrentBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto currentDto = new BookingDto();
        currentDto.setItemId(item.getId());
        currentDto.setStartDate(now.minusHours(1));
        currentDto.setEndDate(now.plusHours(1));
        bookingService.createBooking(booker.getId(), currentDto);

        BookingDto futureDto = new BookingDto();
        futureDto.setItemId(item.getId());
        futureDto.setStartDate(now.plusDays(1));
        futureDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), futureDto);

        List<BookingDto> bookings = bookingService.getBookingsByCreator(booker.getId(), BookingState.CURRENT);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getBookingsByCreator_withStatePast_thenReturnPastBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto pastDto = new BookingDto();
        pastDto.setItemId(item.getId());
        pastDto.setStartDate(now.minusDays(3));
        pastDto.setEndDate(now.minusDays(2));
        bookingService.createBooking(booker.getId(), pastDto);

        BookingDto futureDto = new BookingDto();
        futureDto.setItemId(item.getId());
        futureDto.setStartDate(now.plusDays(1));
        futureDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), futureDto);

        List<BookingDto> bookings = bookingService.getBookingsByCreator(booker.getId(), BookingState.PAST);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getBookingsByCreator_withStateFuture_thenReturnFutureBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto pastDto = new BookingDto();
        pastDto.setItemId(item.getId());
        pastDto.setStartDate(now.minusDays(3));
        pastDto.setEndDate(now.minusDays(2));
        bookingService.createBooking(booker.getId(), pastDto);

        BookingDto futureDto = new BookingDto();
        futureDto.setItemId(item.getId());
        futureDto.setStartDate(now.plusDays(1));
        futureDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), futureDto);

        List<BookingDto> bookings = bookingService.getBookingsByCreator(booker.getId(), BookingState.FUTURE);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getBookingsByCreator_withStateWaiting_thenReturnWaitingBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto waitingDto = new BookingDto();
        waitingDto.setItemId(item.getId());
        waitingDto.setStartDate(now.plusDays(1));
        waitingDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), waitingDto);

        BookingDto approvedDto = new BookingDto();
        approvedDto.setItemId(item.getId());
        approvedDto.setStartDate(now.plusDays(3));
        approvedDto.setEndDate(now.plusDays(4));
        BookingDto created2 = bookingService.createBooking(booker.getId(), approvedDto);
        bookingService.approveBooking(owner.getId(), created2.getId(), true);

        List<BookingDto> bookings = bookingService.getBookingsByCreator(booker.getId(), BookingState.WAITING);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByCreator_withStateRejected_thenReturnRejectedBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto rejectedDto = new BookingDto();
        rejectedDto.setItemId(item.getId());
        rejectedDto.setStartDate(now.plusDays(1));
        rejectedDto.setEndDate(now.plusDays(2));
        BookingDto created = bookingService.createBooking(booker.getId(), rejectedDto);
        bookingService.approveBooking(owner.getId(), created.getId(), false);

        BookingDto waitingDto = new BookingDto();
        waitingDto.setItemId(item.getId());
        waitingDto.setStartDate(now.plusDays(3));
        waitingDto.setEndDate(now.plusDays(4));
        bookingService.createBooking(booker.getId(), waitingDto);

        List<BookingDto> bookings = bookingService.getBookingsByCreator(booker.getId(), BookingState.REJECTED);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void getBookingsByItemOwner_withStateAll_thenReturnAllBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto pastDto = new BookingDto();
        pastDto.setItemId(item.getId());
        pastDto.setStartDate(now.minusDays(3));
        pastDto.setEndDate(now.minusDays(2));
        bookingService.createBooking(booker.getId(), pastDto);

        BookingDto futureDto = new BookingDto();
        futureDto.setItemId(item.getId());
        futureDto.setStartDate(now.plusDays(1));
        futureDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), futureDto);

        List<BookingDto> bookings = bookingService.getBookingsByItemOwner(owner.getId(), BookingState.ALL);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(2);
    }

    @Test
    void getBookingsByItemOwner_withStateCurrent_thenReturnCurrentBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto currentDto = new BookingDto();
        currentDto.setItemId(item.getId());
        currentDto.setStartDate(now.minusHours(1));
        currentDto.setEndDate(now.plusHours(1));
        bookingService.createBooking(booker.getId(), currentDto);

        BookingDto futureDto = new BookingDto();
        futureDto.setItemId(item.getId());
        futureDto.setStartDate(now.plusDays(1));
        futureDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), futureDto);

        List<BookingDto> bookings = bookingService.getBookingsByItemOwner(owner.getId(), BookingState.CURRENT);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getBookingsByItemOwner_withStatePast_thenReturnPastBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto pastDto = new BookingDto();
        pastDto.setItemId(item.getId());
        pastDto.setStartDate(now.minusDays(3));
        pastDto.setEndDate(now.minusDays(2));
        bookingService.createBooking(booker.getId(), pastDto);

        BookingDto futureDto = new BookingDto();
        futureDto.setItemId(item.getId());
        futureDto.setStartDate(now.plusDays(1));
        futureDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), futureDto);

        List<BookingDto> bookings = bookingService.getBookingsByItemOwner(owner.getId(), BookingState.PAST);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getBookingsByItemOwner_withStateFuture_thenReturnFutureBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto pastDto = new BookingDto();
        pastDto.setItemId(item.getId());
        pastDto.setStartDate(now.minusDays(3));
        pastDto.setEndDate(now.minusDays(2));
        bookingService.createBooking(booker.getId(), pastDto);

        BookingDto futureDto = new BookingDto();
        futureDto.setItemId(item.getId());
        futureDto.setStartDate(now.plusDays(1));
        futureDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), futureDto);

        List<BookingDto> bookings = bookingService.getBookingsByItemOwner(owner.getId(), BookingState.FUTURE);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getBookingsByItemOwner_withStateWaiting_thenReturnWaitingBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto waitingDto = new BookingDto();
        waitingDto.setItemId(item.getId());
        waitingDto.setStartDate(now.plusDays(1));
        waitingDto.setEndDate(now.plusDays(2));
        bookingService.createBooking(booker.getId(), waitingDto);

        BookingDto approvedDto = new BookingDto();
        approvedDto.setItemId(item.getId());
        approvedDto.setStartDate(now.plusDays(3));
        approvedDto.setEndDate(now.plusDays(4));
        BookingDto created2 = bookingService.createBooking(booker.getId(), approvedDto);
        bookingService.approveBooking(owner.getId(), created2.getId(), true);

        List<BookingDto> bookings = bookingService.getBookingsByItemOwner(owner.getId(), BookingState.WAITING);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByItemOwner_withStateRejected_thenReturnRejectedBookings() {
        LocalDateTime now = LocalDateTime.now();

        BookingDto rejectedDto = new BookingDto();
        rejectedDto.setItemId(item.getId());
        rejectedDto.setStartDate(now.plusDays(1));
        rejectedDto.setEndDate(now.plusDays(2));
        BookingDto created = bookingService.createBooking(booker.getId(), rejectedDto);
        bookingService.approveBooking(owner.getId(), created.getId(), false);

        BookingDto waitingDto = new BookingDto();
        waitingDto.setItemId(item.getId());
        waitingDto.setStartDate(now.plusDays(3));
        waitingDto.setEndDate(now.plusDays(4));
        bookingService.createBooking(booker.getId(), waitingDto);

        List<BookingDto> bookings = bookingService.getBookingsByItemOwner(owner.getId(), BookingState.REJECTED);

        assertThat(bookings).isNotNull();
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void getBookingById_whenBookingNotFound_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(booker.getId(), 999L);
        });
    }

    @Test
    void approveBooking_whenBookingNotFound_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.approveBooking(owner.getId(), 999L, true);
        });
    }

    @Test
    void getBookingsByCreator_whenUserNotFound_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsByCreator(999L, BookingState.ALL);
        });
    }

    @Test
    void getBookingsByItemOwner_whenUserNotFound_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsByItemOwner(999L, BookingState.ALL);
        });
    }
}


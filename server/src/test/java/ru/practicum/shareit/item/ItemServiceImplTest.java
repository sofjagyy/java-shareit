package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private User booker;
    private Item item1;
    private Item item2;

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

        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        item1 = itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2 = itemRepository.save(item2);

        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = new Booking();
        lastBooking.setStartDate(now.minusDays(2));
        lastBooking.setEndDate(now.minusDays(1));
        lastBooking.setItem(item1);
        lastBooking.setCreator(booker);
        lastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(lastBooking);

        Booking nextBooking = new Booking();
        nextBooking.setStartDate(now.plusDays(1));
        nextBooking.setEndDate(now.plusDays(2));
        nextBooking.setItem(item1);
        nextBooking.setCreator(booker);
        nextBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(nextBooking);
    }

    @Test
    void getItemsByOwner_whenOwnerHasItemsWithBookings_thenReturnItemsWithBookingsInfo() {
        List<ItemWithBookingsDto> items = itemService.getItemsByOwner(owner.getId());

        assertThat(items).isNotNull();
        assertThat(items).hasSize(2);

        ItemWithBookingsDto itemWithBookings = items.stream()
                .filter(i -> i.getId().equals(item1.getId()))
                .findFirst()
                .orElse(null);

        assertThat(itemWithBookings).isNotNull();
        assertThat(itemWithBookings.getName()).isEqualTo("Item 1");
        assertThat(itemWithBookings.getDescription()).isEqualTo("Description 1");
        assertThat(itemWithBookings.getAvailable()).isTrue();
        assertThat(itemWithBookings.getLastBooking()).isNotNull();
        assertThat(itemWithBookings.getLastBooking().getCreatorId()).isEqualTo(booker.getId());
        assertThat(itemWithBookings.getNextBooking()).isNotNull();
        assertThat(itemWithBookings.getNextBooking().getCreatorId()).isEqualTo(booker.getId());

        ItemWithBookingsDto item2Dto = items.stream()
                .filter(i -> i.getId().equals(item2.getId()))
                .findFirst()
                .orElse(null);

        assertThat(item2Dto).isNotNull();
        assertThat(item2Dto.getName()).isEqualTo("Item 2");
        assertThat(item2Dto.getLastBooking()).isNull();
        assertThat(item2Dto.getNextBooking()).isNull();
    }

    @Test
    void addItem_whenValidDataWithoutRequest_thenItemCreated() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("New Description");
        itemDto.setAvailable(true);

        ItemDto result = itemService.addItem(owner.getId(), itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("New Item");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getAvailable()).isTrue();
        assertThat(result.getRequestId()).isNull();
    }

    @Test
    void addItem_whenValidDataWithRequest_thenItemCreatedWithRequestLink() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Need a tool");
        request.setCreator(booker);
        request.setCreatedAt(LocalDateTime.now());
        request = itemRequestRepository.save(request);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Tool");
        itemDto.setDescription("Requested tool");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());

        ItemDto result = itemService.addItem(owner.getId(), itemDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Tool");
        assertThat(result.getRequestId()).isEqualTo(request.getId());
    }
}


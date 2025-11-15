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

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void addItem_whenRequestIdNotFound_thenThrowNotFoundException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Tool");
        itemDto.setDescription("Requested tool");
        itemDto.setAvailable(true);
        itemDto.setRequestId(999L);

        assertThrows(NotFoundException.class, () -> {
            itemService.addItem(owner.getId(), itemDto);
        });
    }

    @Test
    void updateItem_whenValidNameUpdate_thenItemUpdated() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");

        ItemDto updatedItem = itemService.updateItem(owner.getId(), item1.getId(), updateDto);

        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo("Updated Name");
        assertThat(updatedItem.getDescription()).isEqualTo("Description 1");
    }

    @Test
    void updateItem_whenValidDescriptionUpdate_thenItemUpdated() {
        ItemDto updateDto = new ItemDto();
        updateDto.setDescription("Updated Description");

        ItemDto updatedItem = itemService.updateItem(owner.getId(), item1.getId(), updateDto);

        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo("Item 1");
        assertThat(updatedItem.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    void updateItem_whenValidAvailableUpdate_thenItemUpdated() {
        ItemDto updateDto = new ItemDto();
        updateDto.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(owner.getId(), item1.getId(), updateDto);

        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void updateItem_whenNotOwner_thenThrowForbiddenException() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");

        assertThrows(ForbiddenException.class, () -> {
            itemService.updateItem(booker.getId(), item1.getId(), updateDto);
        });
    }

    @Test
    void updateItem_whenItemNotFound_thenThrowNotFoundException() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Name");

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(owner.getId(), 999L, updateDto);
        });
    }

    @Test
    void getItemById_whenOwnerRequests_thenReturnWithBookings() {
        ItemWithBookingsDto result = itemService.getItemById(owner.getId(), item1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item1.getId());
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
    }

    @Test
    void getItemById_whenOtherUserRequests_thenReturnWithoutBookings() {
        ItemWithBookingsDto result = itemService.getItemById(booker.getId(), item1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item1.getId());
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
    }

    @Test
    void getItemById_whenItemNotFound_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(owner.getId(), 999L);
        });
    }

    @Test
    void searchItems_whenTextIsBlank_thenReturnEmptyList() {
        List<ItemDto> results = itemService.searchItems("");

        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
    }

    @Test
    void searchItems_whenTextMatches_thenReturnMatchingItems() {
        List<ItemDto> results = itemService.searchItems("Item");

        assertThat(results).isNotNull();
        assertThat(results).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void getItemsByOwner_whenOwnerHasNoItems_thenReturnEmptyList() {
        User newOwner = new User();
        newOwner.setName("New Owner");
        newOwner.setEmail("newowner@example.com");
        newOwner = userRepository.save(newOwner);

        List<ItemWithBookingsDto> items = itemService.getItemsByOwner(newOwner.getId());

        assertThat(items).isNotNull();
        assertThat(items).isEmpty();
    }

    @Test
    void searchItems_whenTextIsNull_thenReturnEmptyList() {
        List<ItemDto> results = itemService.searchItems(null);

        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
    }

    @Test
    void searchItems_whenNoMatches_thenReturnEmptyList() {
        List<ItemDto> results = itemService.searchItems("nonexistentitem12345");

        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
    }

    @Test
    void addItem_whenUserNotFound_thenThrowNotFoundException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        assertThrows(NotFoundException.class, () -> {
            itemService.addItem(999L, itemDto);
        });
    }
}


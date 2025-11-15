package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User itemOwner;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setName("Requester");
        requester.setEmail("requester@example.com");
        requester = userRepository.save(requester);

        itemOwner = new User();
        itemOwner.setName("Item Owner");
        itemOwner.setEmail("owner@example.com");
        itemOwner = userRepository.save(itemOwner);

        request1 = new ItemRequest();
        request1.setDescription("Need a drill");
        request1.setCreator(requester);
        request1.setCreatedAt(LocalDateTime.now().minusDays(2));
        request1 = itemRequestRepository.save(request1);

        request2 = new ItemRequest();
        request2.setDescription("Need a ladder");
        request2.setCreator(requester);
        request2.setCreatedAt(LocalDateTime.now().minusDays(1));
        request2 = itemRequestRepository.save(request2);

        Item item1 = new Item();
        item1.setName("Electric Drill");
        item1.setDescription("Powerful drill");
        item1.setAvailable(true);
        item1.setOwner(itemOwner);
        item1.setRequest(request1);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Another Drill");
        item2.setDescription("Another option");
        item2.setAvailable(true);
        item2.setOwner(itemOwner);
        item2.setRequest(request1);
        itemRepository.save(item2);
    }

    @Test
    void getUserRequests_whenUserHasRequests_thenReturnRequestsWithItems() {
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(requester.getId());

        assertThat(requests).isNotNull();
        assertThat(requests).hasSize(2);

        ItemRequestDto firstRequest = requests.get(0);
        assertThat(firstRequest.getDescription()).isEqualTo("Need a ladder");
        assertThat(firstRequest.getItems()).isEmpty();

        ItemRequestDto secondRequest = requests.get(1);
        assertThat(secondRequest.getDescription()).isEqualTo("Need a drill");
        assertThat(secondRequest.getItems()).hasSize(2);
        assertThat(secondRequest.getItems().get(0).getName()).isIn("Electric Drill", "Another Drill");
        assertThat(secondRequest.getItems().get(1).getName()).isIn("Electric Drill", "Another Drill");
    }
}


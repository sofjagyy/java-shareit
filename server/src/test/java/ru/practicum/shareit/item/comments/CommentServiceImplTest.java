package ru.practicum.shareit.item.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import ru.practicum.shareit.exception.NotFoundException;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

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

        Booking completedBooking = new Booking();
        completedBooking.setStartDate(LocalDateTime.now().minusDays(3));
        completedBooking.setEndDate(LocalDateTime.now().minusDays(1));
        completedBooking.setItem(item);
        completedBooking.setCreator(booker);
        completedBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(completedBooking);
    }

    @Test
    void addComment_whenValidComment_thenCommentCreated() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto createdComment = commentService.addComment(booker.getId(), item.getId(), commentDto);

        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getId()).isNotNull();
        assertThat(createdComment.getText()).isEqualTo("Great item!");
        assertThat(createdComment.getCreatorName()).isEqualTo("Booker");
        assertThat(createdComment.getCreatedAt()).isNotNull();

        Comment commentInDb = commentRepository.findById(createdComment.getId()).orElse(null);
        assertThat(commentInDb).isNotNull();
        assertThat(commentInDb.getText()).isEqualTo("Great item!");
        assertThat(commentInDb.getCreator().getId()).isEqualTo(booker.getId());
        assertThat(commentInDb.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void addComment_whenUserNotFound_thenThrowNotFoundException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        assertThrows(NotFoundException.class, () -> {
            commentService.addComment(999L, item.getId(), commentDto);
        });
    }

    @Test
    void addComment_whenItemNotFound_thenThrowNotFoundException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        assertThrows(NotFoundException.class, () -> {
            commentService.addComment(booker.getId(), 999L, commentDto);
        });
    }

    @Test
    void addComment_whenNoCompletedBooking_thenThrowIllegalArgumentException() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("newuser@example.com");
        newUser = userRepository.save(newUser);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Long newUserId = newUser.getId();

        assertThrows(IllegalArgumentException.class, () -> {
            commentService.addComment(newUserId, item.getId(), commentDto);
        });
    }

    @Test
    void addComment_whenBookingNotCompleted_thenThrowIllegalArgumentException() {
        User newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("newuser2@example.com");
        newUser = userRepository.save(newUser);

        Booking futureBooking = new Booking();
        futureBooking.setStartDate(LocalDateTime.now().plusDays(1));
        futureBooking.setEndDate(LocalDateTime.now().plusDays(2));
        futureBooking.setItem(item);
        futureBooking.setCreator(newUser);
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Long newUserId = newUser.getId();

        assertThrows(IllegalArgumentException.class, () -> {
            commentService.addComment(newUserId, item.getId(), commentDto);
        });
    }
}


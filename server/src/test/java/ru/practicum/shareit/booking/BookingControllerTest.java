package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void createBooking_whenValidBooking_thenReturnsCreatedBooking() throws Exception {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto inputDto = new BookingDto();
        inputDto.setItemId(1L);
        inputDto.setStartDate(start);
        inputDto.setEndDate(end);

        BookingDto outputDto = new BookingDto();
        outputDto.setId(1L);
        outputDto.setStartDate(start);
        outputDto.setEndDate(end);
        outputDto.setStatus(BookingStatus.WAITING);
        outputDto.setItem(new ItemDto(1L, "Drill", "Power drill", true, null));
        outputDto.setCreator(new UserDto(1L, "John Doe", "john@example.com"));

        when(bookingService.createBooking(eq(userId), any(BookingDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.booker.id").value(1));

        verify(bookingService, times(1)).createBooking(eq(userId), any(BookingDto.class));
    }

    @Test
    void approveBooking_whenOwnerApproves_thenReturnsApprovedBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        BookingDto outputDto = new BookingDto();
        outputDto.setId(1L);
        outputDto.setStatus(BookingStatus.APPROVED);
        outputDto.setItem(new ItemDto(1L, "Drill", "Power drill", true, null));
        outputDto.setCreator(new UserDto(2L, "Jane Doe", "jane@example.com"));

        when(bookingService.approveBooking(userId, bookingId, approved)).thenReturn(outputDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).approveBooking(userId, bookingId, approved);
    }

    @Test
    void getBookingById_whenBookingExists_thenReturnsBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStartDate(LocalDateTime.now().plusDays(1));
        bookingDto.setEndDate(LocalDateTime.now().plusDays(2));
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setItem(new ItemDto(1L, "Drill", "Power drill", true, null));
        bookingDto.setCreator(new UserDto(1L, "John Doe", "john@example.com"));

        when(bookingService.getBookingById(userId, bookingId)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1));

        verify(bookingService, times(1)).getBookingById(userId, bookingId);
    }

    @Test
    void getBookingsByCreator_whenBookingsExist_thenReturnsBookingList() throws Exception {
        Long userId = 1L;
        BookingState state = BookingState.ALL;

        List<BookingDto> bookings = Arrays.asList(
                createBookingDto(1L, BookingStatus.WAITING),
                createBookingDto(2L, BookingStatus.APPROVED)
        );

        when(bookingService.getBookingsByCreator(userId, state)).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));

        verify(bookingService, times(1)).getBookingsByCreator(userId, state);
    }

    @Test
    void getBookingsByItemOwner_whenBookingsExist_thenReturnsBookingList() throws Exception {
        Long userId = 1L;
        BookingState state = BookingState.ALL;

        List<BookingDto> bookings = Arrays.asList(
                createBookingDto(1L, BookingStatus.WAITING),
                createBookingDto(2L, BookingStatus.APPROVED)
        );

        when(bookingService.getBookingsByItemOwner(userId, state)).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, userId)
                        .param("state", state.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(bookingService, times(1)).getBookingsByItemOwner(userId, state);
    }

    private BookingDto createBookingDto(Long id, BookingStatus status) {
        BookingDto dto = new BookingDto();
        dto.setId(id);
        dto.setStartDate(LocalDateTime.now().plusDays(1));
        dto.setEndDate(LocalDateTime.now().plusDays(2));
        dto.setStatus(status);
        dto.setItem(new ItemDto(1L, "Drill", "Power drill", true, null));
        dto.setCreator(new UserDto(2L, "Jane Doe", "jane@example.com"));
        return dto;
    }
}



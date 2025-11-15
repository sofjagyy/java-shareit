package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void createRequest_whenValidRequest_thenReturnsCreatedRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setDescription("Need a drill");

        ItemRequestDto outputDto = new ItemRequestDto();
        outputDto.setId(1L);
        outputDto.setDescription("Need a drill");
        outputDto.setCreated(LocalDateTime.now());
        outputDto.setItems(Collections.emptyList());

        when(itemRequestService.createRequest(eq(userId), any(ItemRequestDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/requests")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.created").exists());

        verify(itemRequestService, times(1)).createRequest(eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void getUserRequests_whenUserHasRequests_thenReturnsRequestList() throws Exception {
        Long userId = 1L;

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("Need a drill");
        request1.setCreated(LocalDateTime.now());
        request1.setItems(Arrays.asList(
                new ItemDto(1L, "Electric Drill", "Power drill", true, 1L)
        ));

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(2L);
        request2.setDescription("Need a ladder");
        request2.setCreated(LocalDateTime.now());
        request2.setItems(Collections.emptyList());

        List<ItemRequestDto> requests = Arrays.asList(request1, request2);

        when(itemRequestService.getUserRequests(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"))
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Need a ladder"))
                .andExpect(jsonPath("$[1].items.length()").value(0));

        verify(itemRequestService, times(1)).getUserRequests(userId);
    }

    @Test
    void getAllRequests_whenRequestsExist_thenReturnsAllRequests() throws Exception {
        Long userId = 1L;

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("Need a drill");
        request1.setCreated(LocalDateTime.now());
        request1.setItems(Collections.emptyList());

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(2L);
        request2.setDescription("Need a saw");
        request2.setCreated(LocalDateTime.now());
        request2.setItems(Collections.emptyList());

        List<ItemRequestDto> requests = Arrays.asList(request1, request2);

        when(itemRequestService.getAllRequests(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Need a drill"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Need a saw"));

        verify(itemRequestService, times(1)).getAllRequests(userId);
    }

    @Test
    void getRequestById_whenRequestExists_thenReturnsRequest() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a drill");
        requestDto.setCreated(LocalDateTime.now());
        requestDto.setItems(Arrays.asList(
                new ItemDto(1L, "Electric Drill", "Power drill", true, 1L),
                new ItemDto(2L, "Hand Drill", "Manual drill", true, 1L)
        ));

        when(itemRequestService.getRequestById(userId, requestId)).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].name").value("Electric Drill"))
                .andExpect(jsonPath("$.items[1].name").value("Hand Drill"));

        verify(itemRequestService, times(1)).getRequestById(userId, requestId);
    }
}



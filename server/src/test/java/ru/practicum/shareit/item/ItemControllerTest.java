package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Test
    void addItem_whenValidItem_thenReturnsCreatedItem() throws Exception {
        Long userId = 1L;
        ItemDto inputDto = new ItemDto(null, "Drill", "Power drill", true, null);
        ItemDto outputDto = new ItemDto(1L, "Drill", "Power drill", true, null);

        when(itemService.addItem(eq(userId), any(ItemDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/items")
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Power drill"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).addItem(eq(userId), any(ItemDto.class));
    }

    @Test
    void updateItem_whenValidUpdate_thenReturnsUpdatedItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto inputDto = new ItemDto(null, "Updated Drill", null, null, null);
        ItemDto outputDto = new ItemDto(1L, "Updated Drill", "Power drill", true, null);

        when(itemService.updateItem(eq(userId), eq(itemId), any(ItemDto.class))).thenReturn(outputDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Drill"))
                .andExpect(jsonPath("$.description").value("Power drill"));

        verify(itemService, times(1)).updateItem(eq(userId), eq(itemId), any(ItemDto.class));
    }

    @Test
    void getItemById_whenItemExists_thenReturnsItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemWithBookingsDto itemDto = new ItemWithBookingsDto(
                1L, "Drill", "Power drill", true, null, null, null, Collections.emptyList()
        );

        when(itemService.getItemById(userId, itemId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Power drill"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).getItemById(userId, itemId);
    }

    @Test
    void getItemsByOwner_whenOwnerHasItems_thenReturnsItemList() throws Exception {
        Long userId = 1L;
        List<ItemWithBookingsDto> items = Arrays.asList(
                new ItemWithBookingsDto(1L, "Drill", "Power drill", true, null, null, null, Collections.emptyList()),
                new ItemWithBookingsDto(2L, "Saw", "Hand saw", true, null, null, null, Collections.emptyList())
        );

        when(itemService.getItemsByOwner(userId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Saw"));

        verify(itemService, times(1)).getItemsByOwner(userId);
    }

    @Test
    void searchItems_whenTextProvided_thenReturnsMatchingItems() throws Exception {
        String searchText = "drill";
        List<ItemDto> items = Arrays.asList(
                new ItemDto(1L, "Drill", "Power drill", true, null),
                new ItemDto(2L, "Electric Drill", "Cordless drill", true, null)
        );

        when(itemService.searchItems(searchText)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].name").value("Electric Drill"));

        verify(itemService, times(1)).searchItems(searchText);
    }

    @Test
    void addComment_whenValidComment_thenReturnsCreatedComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto inputDto = new CommentDto(null, "Great item!", null, null);
        CommentDto outputDto = new CommentDto(
                1L, "Great item!", "John Doe", LocalDateTime.now()
        );

        when(commentService.addComment(eq(userId), eq(itemId), any(CommentDto.class)))
                .thenReturn(outputDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("John Doe"));

        verify(commentService, times(1)).addComment(eq(userId), eq(itemId), any(CommentDto.class));
    }
}



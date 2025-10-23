package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        ItemRequest created = itemRequestService.create(itemRequest, userId);
        return ItemRequestMapper.toItemRequestDto(created, Collections.emptyList());
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findByRequestorId(userId).stream()
                .map(request -> {
                    List<ItemDto> items = itemService.allItems().stream()
                            .filter(item -> item.getRequest() != null &&
                                    item.getRequest().getId().equals(request.getId()))
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findAllExceptUser(userId).stream()
                .map(request -> {
                    List<ItemDto> items = itemService.allItems().stream()
                            .filter(item -> item.getRequest() != null &&
                                    item.getRequest().getId().equals(request.getId()))
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable Long requestId) {
        ItemRequest itemRequest = itemRequestService.getRequest(requestId);

        List<ItemDto> items = itemService.allItems().stream()
                .filter(item -> item.getRequest() != null &&
                        item.getRequest().getId().equals(requestId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }
}


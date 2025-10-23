package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return ItemMapper.toItemDto(itemService.item(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена")));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Validated(ItemDto.Create.class) @RequestBody ItemDto itemDto) {
        User owner = userService.user(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItemWithOwner(itemDto, owner);
        
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestService.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос вещи не найден"));
            item.setRequest(itemRequest);
        }
        
        return ItemMapper.toItemDto(itemService.save(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        Item existingItem = itemService.item(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является владельцем вещи");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        item.setOwner(existingItem.getOwner());
        if (item.getName() == null) {
            item.setName(existingItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(existingItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(existingItem.getAvailable());
        }
        return ItemMapper.toItemDto(itemService.save(item));
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        Item item = itemService.item(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является владельцем вещи");
        }
        itemService.delete(item);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId,
                                  @Validated @RequestBody CommentDto commentDto) {
        Item item = itemService.item(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        User author = userService.user(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        
        return CommentMapper.toCommentDto(itemService.addComment(comment, userId));
    }
}


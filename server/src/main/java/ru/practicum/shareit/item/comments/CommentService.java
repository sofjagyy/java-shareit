package ru.practicum.shareit.item.comments;

public interface CommentService {
    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}


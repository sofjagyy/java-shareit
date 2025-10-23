package ru.practicum.shareit.item.comment;

import java.util.List;

public interface CommentRepository {
    Comment save(Comment comment);

    List<Comment> findByItemId(Long itemId);
}


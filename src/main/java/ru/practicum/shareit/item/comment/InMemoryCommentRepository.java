package ru.practicum.shareit.item.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryCommentRepository implements CommentRepository {
    private final Map<Long, Comment> comments = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.setId(currentId.getAndIncrement());
        }
        comments.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public List<Comment> findByItemId(Long itemId) {
        return comments.values().stream()
                .filter(comment -> comment.getItem() != null && comment.getItem().getId().equals(itemId))
                .collect(Collectors.toList());
    }
}


package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private String text;
    private Item item;
    private User author;
    private LocalDateTime created;
}


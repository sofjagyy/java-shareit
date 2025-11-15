package ru.practicum.shareit.item.comments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;

    @JsonProperty("authorName")
    private String creatorName;

    @JsonProperty("created")
    private LocalDateTime createdAt;
}
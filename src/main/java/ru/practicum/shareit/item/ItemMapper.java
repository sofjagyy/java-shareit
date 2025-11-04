package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface ItemMapper {

    @Mapping(target = "requestId", source = "request.id")
    ItemDto toDto(Item item);

    List<ItemDto> toDto(List<Item> items);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item toEntity(ItemDto dto);

    default ItemWithBookingsDto toDtoWithBookings(
            Item item,
            Booking lastBooking,
            Booking nextBooking,
            List<Comment> comments) {

        if (item == null) {
            return null;
        }

        return new ItemWithBookingsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                toBookingShortDto(lastBooking),
                toBookingShortDto(nextBooking),
                commentListToCommentDtoList(comments)
        );
    }

    @Mapping(target = "bookerId", source = "creator.id")
    BookingShortDto toBookingShortDto(Booking booking);

    List<CommentDto> commentListToCommentDtoList(List<Comment> comments);
}
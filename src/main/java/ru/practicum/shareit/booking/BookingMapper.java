package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    BookingDto toDto(Booking booking);

    List<BookingDto> toDto(List<Booking> bookings);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    Booking toEntity(BookingDto dto);

    @Mapping(target = "requestId", source = "request.id")
    ItemDto toItemDto(Item item);

    UserDto toUserDto(User user);
}


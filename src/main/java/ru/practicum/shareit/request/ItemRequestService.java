package ru.practicum.shareit.request;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    ItemRequest create(ItemRequest itemRequest, Long requestorId);

    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findByRequestorId(Long requestorId);

    List<ItemRequest> findAllExceptUser(Long userId);
}


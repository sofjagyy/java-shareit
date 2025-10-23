package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRequestRepository implements ItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @Override
    public ItemRequest save(ItemRequest itemRequest) {
        if (itemRequest.getId() == null) {
            itemRequest.setId(currentId.getAndIncrement());
        }
        requests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    @Override
    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(requests.get(id));
    }

    @Override
    public List<ItemRequest> findByRequestorId(Long requestorId) {
        return requests.values().stream()
                .filter(request -> request.getRequestor() != null &&
                        request.getRequestor().getId().equals(requestorId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequest> findAll() {
        return new ArrayList<>(requests.values());
    }
}


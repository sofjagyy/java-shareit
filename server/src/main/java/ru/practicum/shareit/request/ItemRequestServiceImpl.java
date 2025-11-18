package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestDto dto) {
        User creator = getUserById(userId);

        ItemRequest itemRequest = itemRequestMapper.toEntity(dto);
        itemRequest.setCreator(creator);
        itemRequest.setCreatedAt(LocalDateTime.now());

        itemRequest = itemRequestRepository.save(itemRequest);

        return itemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        getUserById(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByCreatorIdOrderByCreatedAtDesc(userId);

        return enrichWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        getUserById(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByCreatorIdNotOrderByCreatedAtDesc(userId);

        return enrichWithItems(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        getUserById(userId);

        ItemRequest itemRequest = getRequest(requestId);

        ItemRequestDto dto = itemRequestMapper.toDto(itemRequest);

        List<Item> items = itemRepository.findByRequestId(requestId);
        dto.setItems(itemMapper.toDto(items));

        return dto;
    }

    private List<ItemRequestDto> enrichWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> allItems = requestIds.stream()
                .flatMap(id -> itemRepository.findByRequestId(id).stream())
                .toList();

        Map<Long, List<Item>> itemsByRequestId = allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = itemRequestMapper.toDto(request);
                    List<Item> items = itemsByRequestId.getOrDefault(request.getId(), List.of());
                    dto.setItems(itemMapper.toDto(items));
                    return dto;
                })
                .toList();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private ItemRequest getRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
    }
}


package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    public ItemRequest create(ItemRequest itemRequest, Long requestorId) {
        User requestor = userService.user(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public Optional<ItemRequest> findById(Long id) {
        return itemRequestRepository.findById(id);
    }

    @Override
    public List<ItemRequest> findByRequestorId(Long requestorId) {
        return itemRequestRepository.findByRequestorId(requestorId);
    }

    @Override
    public List<ItemRequest> findAllExceptUser(Long userId) {
        return itemRequestRepository.findAll().stream()
                .filter(request -> !request.getRequestor().getId().equals(userId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }
}


package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {

	ItemRequestDto addRequest(Long userId, NewItemRequestDto dto);

	List<ItemRequestDto> getUserRequests(Long userId);

	List<ItemRequestDto> getAllRequests(Long userId, int from, int size);

	ItemRequestDto getRequestById(Long userId, Long requestId);
}

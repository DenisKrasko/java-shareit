package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
	public static final String USER_ID = "X-Sharer-User-Id";

	private final ItemRequestService itemRequestService;

	@PostMapping
	public ItemRequestDto addRequest(@RequestHeader(USER_ID) Long userId,
									 @RequestBody NewItemRequestDto newItemRequestDto) {
		return itemRequestService.addRequest(userId, newItemRequestDto);
	}

	@GetMapping
	public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID) Long userId) {
		return itemRequestService.getUserRequests(userId);
	}

	@GetMapping("/all")
	public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID) Long userId,
											   @RequestParam(defaultValue = "0") Integer from,
											   @RequestParam(defaultValue = "10") Integer size) {
		return itemRequestService.getAllRequests(userId, from, size);
	}

	@GetMapping("/{requestId}")
	public ItemRequestDto getRequestById(@RequestHeader(USER_ID) Long userId,
										 @PathVariable Long requestId) {
		return itemRequestService.getRequestById(userId, requestId);
	}
}

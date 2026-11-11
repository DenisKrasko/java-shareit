package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
	public static final String USER_ID = "X-Sharer-User-Id";

	private final ItemRequestClient itemRequestClient;

	@PostMapping
	public ResponseEntity<Object> addRequest(@RequestHeader(USER_ID) long userId,
											 @Valid @RequestBody NewItemRequestDto newItemRequestDto) {
		return itemRequestClient.addRequest(userId, newItemRequestDto);
	}

	@GetMapping
	public ResponseEntity<Object> getUserRequests(@RequestHeader(USER_ID) long userId) {
		return itemRequestClient.getUserRequests(userId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getAllRequests(
			@RequestHeader(USER_ID) long userId,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		return itemRequestClient.getAllRequests(userId, from, size);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID) long userId,
												 @PathVariable Long requestId) {
		return itemRequestClient.getRequestById(userId, requestId);
	}
}

package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
	public static final String USER_ID = "X-Sharer-User-Id";

	private final ItemClient itemClient;

	@PostMapping
	public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) Long userId,
										  @Valid @RequestBody ItemDto itemDto) {
		return itemClient.addItem(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> update(@RequestHeader(USER_ID) Long userId,
										 @PathVariable Long itemId,
										 @Valid @RequestBody UpdateItemDto itemDto) {
		return itemClient.updateItem(userId, itemId, itemDto);
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID) Long userId,
											  @PathVariable Long itemId) {
		return itemClient.getItem(userId, itemId);
	}

	@GetMapping
	public ResponseEntity<Object> getItemsByOwner(@RequestHeader(USER_ID) Long userId) {
		return itemClient.getItems(userId);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> searchItems(@RequestHeader(USER_ID) Long userId,
											  @RequestParam String text) {
		if (text == null || text.isBlank()) {
			return ResponseEntity.ok(List.of());
		}
		return itemClient.searchItems(userId, text);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) Long userId,
											 @PathVariable Long itemId,
											 @Valid @RequestBody CommentRequestDto commentDto) {
		return itemClient.addComment(userId, itemId, commentDto);
	}
}

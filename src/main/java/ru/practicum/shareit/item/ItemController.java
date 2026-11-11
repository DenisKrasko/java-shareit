package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
	public static final String USER_ID = "X-Sharer-User-Id";

	private final ItemService itemService;

	@PostMapping
	public ItemDto addItem(@RequestHeader(USER_ID) Long userId,
						   @Valid @RequestBody ItemDto itemDto) {
		return itemService.addItem(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ItemDto update(@RequestHeader(USER_ID) Long userId,
						  @PathVariable Long itemId,
						  @RequestBody ItemDto itemDto) {
		return itemService.update(userId, itemId, itemDto);
	}

	@GetMapping("/{itemId}")
	public ItemWithBookingDto getItemById(@RequestHeader(USER_ID) Long userId,
										  @PathVariable Long itemId) {
		return itemService.getItemById(itemId, userId);
	}

	@GetMapping
	public List<ItemWithBookingDto> getItemsByOwner(@RequestHeader(USER_ID) Long userId) {
		return itemService.getItemsByOwner(userId);
	}

	@GetMapping("/search")
	public List<ItemDto> searchItems(@RequestParam String text) {
		return itemService.search(text);
	}

	@PostMapping("/{itemId}/comment")
	public CommentDto addComment(@RequestHeader(USER_ID) Long userId,
								 @PathVariable Long itemId,
								 @Valid @RequestBody CommentDto commentDto) {
		return itemService.addComment(userId, itemId, commentDto);
	}
}

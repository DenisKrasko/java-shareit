package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
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
	public ItemDto getItemById(@PathVariable Long itemId) {
		return itemService.getItemById(itemId);
	}

	@GetMapping
	public List<ItemDto> getOwnerList(@RequestHeader(USER_ID) Long userId) {
		return itemService.getOwnerItems(userId);
	}

	@GetMapping("/search")
	public List<ItemDto> searchItems(@RequestParam String text) {
		return itemService.searchItems(text);
	}
}

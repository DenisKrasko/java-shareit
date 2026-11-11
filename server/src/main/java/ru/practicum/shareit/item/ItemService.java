package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
	ItemDto addItem(Long userId, ItemDto itemDto);

	ItemDto update(Long userId, Long itemId, ItemDto itemDto);

	ItemWithBookingDto getItemById(Long itemId, Long userId);

	List<ItemWithBookingDto> getItemsByOwner(Long userId);

	List<ItemDto> search(String text);

	CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
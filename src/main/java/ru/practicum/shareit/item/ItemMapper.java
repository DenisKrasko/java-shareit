package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
	public static ItemDto toItemDto(Item item) {
		return new ItemDto(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.isAvailable(),
				item.getRequest() != null ? item.getRequest() : null
		);
	}

	public static ItemWithBookingDto toItemWithBookingDto(Item item) {
		if (item == null) {
			return null;
		}
		ItemWithBookingDto dto = new ItemWithBookingDto();
		dto.setId(item.getId());
		dto.setName(item.getName());
		dto.setDescription(item.getDescription());
		dto.setAvailable(item.isAvailable());
		return dto;
	}

	public static Item toItem(ItemDto itemDto) {
		return new Item(
				itemDto.getId(),
				itemDto.getName(),
				itemDto.getDescription(),
				itemDto.getAvailable(),
				null,
				null
		);
	}
}
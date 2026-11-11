package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

	public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemRequestItemDto> items) {
		return new ItemRequestDto(
				request.getId(),
				request.getDescription(),
				request.getCreated(),
				items
		);
	}
}

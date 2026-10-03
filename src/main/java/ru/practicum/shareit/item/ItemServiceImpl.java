package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

	@Override
	public ItemDto addItem(Long userId, ItemDto itemDto) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
		Item item = ItemMapper.toItem(itemDto);
		item.setOwner(user);
		Item savedItem = itemRepository.save(item);
		return ItemMapper.toItemDto(savedItem);
	}

	@Override
	public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
		Item itemToUpdate = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
		if (!itemToUpdate.getOwner().getId().equals(userId)) {
			throw new NotFoundException("Пользователь с id " + userId + " не является владельцем вещи");
		}
		if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
			itemToUpdate.setName(itemDto.getName());
		}
		if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
			itemToUpdate.setDescription(itemDto.getDescription());
		}
		if (itemDto.getAvailable() != null) {
			itemToUpdate.setAvailable(itemDto.getAvailable());
		}
		Item updatedItem = itemRepository.save(itemToUpdate);
		return ItemMapper.toItemDto(updatedItem);
	}

	@Override
	public ItemDto getItemById(Long itemId) {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
		return ItemMapper.toItemDto(item);
	}

	@Override
	public List<ItemDto> getOwnerItems(Long userId) {
		return itemRepository.findAll().stream()
				.filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public List<ItemDto> searchItems(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}
		List<Item> items = itemRepository.search(text);
		return items.stream()
				.map(ItemMapper::toItemDto)
				.collect(Collectors.toList());
	}
}
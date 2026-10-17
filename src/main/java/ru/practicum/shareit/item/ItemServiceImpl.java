package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

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
		Item updatedItem = itemRepository.update(itemToUpdate);
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
		String query = text.toLowerCase();
		return itemRepository.findAll().stream()
				.filter(Item::isAvailable)
				.filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(query)) ||
						(item.getDescription() != null && item.getDescription().toLowerCase().contains(query)))
				.map(ItemMapper::toItemDto)
				.toList();
	}
}
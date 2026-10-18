package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;
	private final BookingRepository bookingRepository;
	private final CommentRepository commentRepository;

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
		if (itemToUpdate.getOwner() == null || !itemToUpdate.getOwner().getId().equals(userId)) {
			throw new NotFoundException("Вещь с id " + itemId + " не принадлежит пользователю с id " + userId);
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
	public ItemWithBookingDto getItemById(Long itemId, Long userId) {
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
		ItemWithBookingDto dto = ItemMapper.toItemWithBookingDto(item);
		if (item.getOwner().getId().equals(userId)) {
			List<Booking> bookings = bookingRepository.findByItemIdAndStatus(
					itemId, BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"));
			enrichWithBookings(dto, bookings);
		}
		enrichWithComments(dto, commentRepository.findAllByItemId(itemId));
		return dto;
	}

	@Override
	public List<ItemWithBookingDto> getItemsByOwner(Long userId) {
		List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
		if (items.isEmpty()) return List.of();

		List<Booking> bookings = bookingRepository
				.findApprovedWithItemAndBookerByOwnerId(userId);

		List<Comment> comments = commentRepository.findWithAuthorByOwnerId(userId);

		return items.stream()
				.map(item -> {
					ItemWithBookingDto dto = ItemMapper.toItemWithBookingDto(item);
					enrichWithBookings(dto, filterBookingsByItemId(bookings, item.getId()));
					enrichWithComments(dto, filterCommentsByItemId(comments, item.getId()));
					return dto;
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<ItemDto> search(String text) {
		if (text == null || text.isBlank()) {
			return List.of();
		}
		List<Item> items = itemRepository.search(text);
		return items.stream()
				.map(ItemMapper::toItemDto)
				.collect(Collectors.toList());
	}

	private void enrichWithBookings(ItemWithBookingDto dto, List<Booking> bookings) {
		LocalDateTime now = LocalDateTime.now();

		Booking lastBooking = bookings.stream()
				.filter(b -> !b.getStart().isAfter(now))
				.max((a, b) -> a.getStart().compareTo(b.getStart()))
				.orElse(null);

		Booking nextBooking = bookings.stream()
				.filter(b -> b.getStart().isAfter(now))
				.min((a, b) -> a.getStart().compareTo(b.getStart()))
				.orElse(null);


		dto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
		dto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
	}

	@Override
	@Transactional
	public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
		User author = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

		if (commentDto.getText() == null || commentDto.getText().isBlank()) {
			throw new ValidationException("Текст комментария не может быть пустым");
		}

		boolean hasEndedBooking = bookingRepository.existsByBooker_IdAndItemIdAndStatusAndEndIsBefore(
				userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
		if (!hasEndedBooking) {
			throw new ValidationException("Оставить отзыв может только арендатор после окончания срока аренды");
		}

		Comment comment = new Comment(null, commentDto.getText(), item, author, LocalDateTime.now());
		return CommentMapper.toCommentDto(commentRepository.save(comment));
	}

	/**
	 * Заполняет DTO вещи списком всех её комментариев.
	 */
	private void enrichWithComments(ItemWithBookingDto dto, List<Comment> comments) {
		dto.setComments(comments.stream()
				.map(CommentMapper::toCommentDto)
				.collect(Collectors.toList()));
	}

	private List<Booking> filterBookingsByItemId(List<Booking> bookings, Long itemId) {
		return bookings.stream()
				.filter(b -> b.getItem().getId().equals(itemId))
				.collect(Collectors.toList());
	}

	private List<Comment> filterCommentsByItemId(List<Comment> comments, Long itemId) {
		return comments.stream()
				.filter(c -> c.getItem().getId().equals(itemId))
				.collect(Collectors.toList());
	}
}
package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceIntegrationTest {
	@Autowired
	private ItemService itemService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private ItemRequestRepository itemRequestRepository;

	@Test
	void shouldCreateItemWithoutRequest() {
		UserDto owner = createUser("owner@example.com");

		ItemDto item = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));

		assertThat(item.getId()).isNotNull();
		assertThat(item.getRequestId()).isNull();
		Item savedItem = itemRepository.findById(item.getId()).orElseThrow();
		assertThat(savedItem.getOwner().getId()).isEqualTo(owner.getId());
	}

	@Test
	void shouldCreateItemForRequest() {
		UserDto owner = createUser("owner@example.com");
		UserDto requestor = createUser("requestor@example.com");
		ItemRequest request = createRequest(requestor.getId(), "Нужна дрель");

		ItemDto item = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, request.getId()));

		assertThat(item.getRequestId()).isEqualTo(request.getId());
	}

	@Test
	void shouldThrowWhenRequestForItemNotFound() {
		UserDto owner = createUser("owner@example.com");

		assertThatThrownBy(() -> itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, 999L)))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void shouldThrowWhenItemOwnerNotFound() {
		assertThatThrownBy(() -> itemService.addItem(999L,
				new ItemDto(null, "Дрель", "Мощная дрель", true, null)))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void shouldUpdateItem() {
		UserDto owner = createUser("owner@example.com");
		ItemDto item = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));

		ItemDto updated = itemService.update(owner.getId(), item.getId(),
				new ItemDto(null, "Новая дрель", "Очень мощная", false, null));

		assertThat(updated.getName()).isEqualTo("Новая дрель");
		assertThat(updated.getDescription()).isEqualTo("Очень мощная");
		assertThat(updated.getAvailable()).isFalse();
	}

	@Test
	void shouldNotUpdateItemByAnotherUser() {
		UserDto owner = createUser("owner@example.com");
		UserDto stranger = createUser("stranger@example.com");
		ItemDto item = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));

		assertThatThrownBy(() -> itemService.update(stranger.getId(), item.getId(),
				new ItemDto(null, "Новая дрель", null, null, null)))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void shouldReturnItemWithBookingsAndCommentsForOwner() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto itemDto = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));
		Item item = itemRepository.findById(itemDto.getId()).orElseThrow();
		User bookerEntity = userRepository.findById(booker.getId()).orElseThrow();

		createBooking(item, bookerEntity,
				LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);
		createBooking(item, bookerEntity,
				LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);
		commentRepository.save(new Comment(null, "Отличная вещь", item, bookerEntity, LocalDateTime.now()));

		ItemWithBookingDto result = itemService.getItemById(item.getId(), owner.getId());

		assertThat(result.getLastBooking()).isNotNull();
		assertThat(result.getNextBooking()).isNotNull();
		assertThat(result.getComments()).hasSize(1);
	}

	@Test
	void shouldHideBookingsForNotOwner() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto itemDto = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));
		Item item = itemRepository.findById(itemDto.getId()).orElseThrow();
		User bookerEntity = userRepository.findById(booker.getId()).orElseThrow();
		createBooking(item, bookerEntity,
				LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.APPROVED);

		ItemWithBookingDto result = itemService.getItemById(item.getId(), booker.getId());

		assertThat(result.getLastBooking()).isNull();
		assertThat(result.getNextBooking()).isNull();
	}

	@Test
	void shouldReturnItemsByOwner() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto first = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));
		itemService.addItem(owner.getId(),
				new ItemDto(null, "Молоток", "Тяжёлый молоток", true, null));
		Item firstItem = itemRepository.findById(first.getId()).orElseThrow();
		User bookerEntity = userRepository.findById(booker.getId()).orElseThrow();
		createBooking(firstItem, bookerEntity,
				LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);
		commentRepository.save(new Comment(null, "Хорошо", firstItem, bookerEntity, LocalDateTime.now()));

		List<ItemWithBookingDto> items = itemService.getItemsByOwner(owner.getId());

		assertThat(items).hasSize(2);
		assertThat(items.get(0).getComments()).hasSize(1);
		assertThat(items.get(0).getLastBooking()).isNotNull();
	}

	@Test
	void shouldReturnEmptyListForOwnerWithoutItems() {
		UserDto owner = createUser("owner@example.com");

		assertThat(itemService.getItemsByOwner(owner.getId())).isEmpty();
	}

	@Test
	void shouldSearchItems() {
		UserDto owner = createUser("owner@example.com");
		itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));
		itemService.addItem(owner.getId(),
				new ItemDto(null, "Инструмент", "Тяжёлый молоток", true, null));
		itemService.addItem(owner.getId(),
				new ItemDto(null, "Лопата", "Недоступная лопата", false, null));

		List<ItemDto> byName = itemService.search("дрель");
		List<ItemDto> byDescription = itemService.search("молоток");
		List<ItemDto> unavailable = itemService.search("лопата");
		List<ItemDto> blank = itemService.search(" ");

		assertThat(byName).hasSize(1);
		assertThat(byDescription).hasSize(1);
		assertThat(unavailable).isEmpty();
		assertThat(blank).isEmpty();
	}

	@Test
	void shouldAddComment() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto itemDto = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));
		Item item = itemRepository.findById(itemDto.getId()).orElseThrow();
		User bookerEntity = userRepository.findById(booker.getId()).orElseThrow();
		createBooking(item, bookerEntity,
				LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);

		CommentDto comment = itemService.addComment(booker.getId(), item.getId(),
				new CommentDto(null, "Отличная вещь", null, null));

		assertThat(comment.getId()).isNotNull();
		assertThat(comment.getText()).isEqualTo("Отличная вещь");
		assertThat(comment.getAuthorName()).isEqualTo(booker.getName());
	}

	@Test
	void shouldNotAddCommentWithoutFinishedBooking() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto item = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));

		assertThatThrownBy(() -> itemService.addComment(booker.getId(), item.getId(),
				new CommentDto(null, "Отличная вещь", null, null)))
				.isInstanceOf(ValidationException.class);
	}

	@Test
	void shouldNotAddEmptyComment() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto item = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));

		assertThatThrownBy(() -> itemService.addComment(booker.getId(), item.getId(),
				new CommentDto(null, " ", null, null)))
				.isInstanceOf(ValidationException.class);
	}

	@Test
	void shouldThrowWhenItemNotFound() {
		UserDto owner = createUser("owner@example.com");

		assertThatThrownBy(() -> itemService.getItemById(999L, owner.getId()))
				.isInstanceOf(NotFoundException.class);
	}

	private UserDto createUser(String email) {
		return userService.addUser(new NewUserRequest("Пользователь", email));
	}

	private ItemRequest createRequest(Long requestorId, String description) {
		User requestor = userRepository.findById(requestorId).orElseThrow();
		return itemRequestRepository.save(new ItemRequest(
				null, description, requestor, LocalDateTime.now()
		));
	}

	private Booking createBooking(Item item, User booker, LocalDateTime start,
								  LocalDateTime end, BookingStatus status) {
		return bookingRepository.save(new Booking(null, start, end, item, booker, status));
	}
}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceIntegrationTest {
	@Autowired
	private BookingService bookingService;

	@Autowired
	private UserService userService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void shouldCreateBooking() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto item = createItem(owner.getId());

		BookingDto booking = bookingService.addBooking(booker.getId(),
				new BookingInputDto(item.getId(), LocalDateTime.now().plusDays(1),
						LocalDateTime.now().plusDays(2)));

		assertThat(booking.getId()).isNotNull();
		assertThat(booking.getStatus()).isEqualTo(BookingStatus.WAITING);
		assertThat(booking.getBooker().getId()).isEqualTo(booker.getId());
		assertThat(booking.getItem().getId()).isEqualTo(item.getId());
	}

	@Test
	void shouldNotBookOwnItem() {
		UserDto owner = createUser("owner@example.com");
		ItemDto item = createItem(owner.getId());

		assertThatThrownBy(() -> bookingService.addBooking(owner.getId(),
				new BookingInputDto(item.getId(), LocalDateTime.now().plusDays(1),
						LocalDateTime.now().plusDays(2))))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void shouldNotBookUnavailableItem() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto item = itemService.addItem(owner.getId(),
				new ItemDto(null, "Дрель", "Мощная дрель", false, null));

		assertThatThrownBy(() -> bookingService.addBooking(booker.getId(),
				new BookingInputDto(item.getId(), LocalDateTime.now().plusDays(1),
						LocalDateTime.now().plusDays(2))))
				.isInstanceOf(ValidationException.class);
	}

	@Test
	void shouldValidateBookingDates() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto item = createItem(owner.getId());
		LocalDateTime start = LocalDateTime.now().plusDays(2);
		LocalDateTime end = LocalDateTime.now().plusDays(1);

		assertThatThrownBy(() -> bookingService.addBooking(booker.getId(),
				new BookingInputDto(item.getId(), start, end)))
				.isInstanceOf(ValidationException.class);

		assertThatThrownBy(() -> bookingService.addBooking(booker.getId(),
				new BookingInputDto(item.getId(), null, end)))
				.isInstanceOf(ValidationException.class);

		assertThatThrownBy(() -> bookingService.addBooking(booker.getId(),
				new BookingInputDto(item.getId(), start, start)))
				.isInstanceOf(ValidationException.class);
	}

	@Test
	void shouldThrowWhenBookingItemNotFound() {
		UserDto booker = createUser("booker@example.com");

		assertThatThrownBy(() -> bookingService.addBooking(booker.getId(),
				new BookingInputDto(999L, LocalDateTime.now().plusDays(1),
						LocalDateTime.now().plusDays(2))))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void shouldThrowWhenBookerNotFound() {
		UserDto owner = createUser("owner@example.com");
		ItemDto item = createItem(owner.getId());

		assertThatThrownBy(() -> bookingService.addBooking(999L,
				new BookingInputDto(item.getId(), LocalDateTime.now().plusDays(1),
						LocalDateTime.now().plusDays(2))))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void shouldApproveAndRejectBooking() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		ItemDto item = createItem(owner.getId());
		BookingDto booking = createBooking(booker.getId(), item.getId());

		BookingDto approved = bookingService.approveBooking(owner.getId(), booking.getId(), true);

		assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);

		BookingDto secondBooking = createBooking(booker.getId(), item.getId());
		BookingDto rejected = bookingService.approveBooking(owner.getId(), secondBooking.getId(), false);

		assertThat(rejected.getStatus()).isEqualTo(BookingStatus.REJECTED);
	}

	@Test
	void shouldNotApproveByWrongUserAndTwice() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		UserDto stranger = createUser("stranger@example.com");
		ItemDto item = createItem(owner.getId());
		BookingDto booking = createBooking(booker.getId(), item.getId());

		assertThatThrownBy(() -> bookingService.approveBooking(stranger.getId(), booking.getId(), true))
				.isInstanceOf(AccessException.class);

		bookingService.approveBooking(owner.getId(), booking.getId(), true);

		assertThatThrownBy(() -> bookingService.approveBooking(owner.getId(), booking.getId(), true))
				.isInstanceOf(ValidationException.class);
	}

	@Test
	void shouldReturnBookingForBookerAndOwner() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		UserDto stranger = createUser("stranger@example.com");
		ItemDto item = createItem(owner.getId());
		BookingDto booking = createBooking(booker.getId(), item.getId());

		assertThat(bookingService.getBookingById(booker.getId(), booking.getId()).getId())
				.isEqualTo(booking.getId());
		assertThat(bookingService.getBookingById(owner.getId(), booking.getId()).getId())
				.isEqualTo(booking.getId());
		assertThatThrownBy(() -> bookingService.getBookingById(stranger.getId(), booking.getId()))
				.isInstanceOf(NotFoundException.class);
		assertThatThrownBy(() -> bookingService.getBookingById(booker.getId(), 999L))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void shouldReturnBookingsByBookerForAllStates() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		User ownerEntity = userRepository.findById(owner.getId()).orElseThrow();
		User bookerEntity = userRepository.findById(booker.getId()).orElseThrow();
		LocalDateTime now = LocalDateTime.now();
		createBookingForState(ownerEntity, bookerEntity, now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);
		createBookingForState(ownerEntity, bookerEntity, now.minusHours(1), now.plusHours(1), BookingStatus.APPROVED);
		createBookingForState(ownerEntity, bookerEntity, now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
		createBookingForState(ownerEntity, bookerEntity, now.minusDays(1), now.plusDays(1), BookingStatus.REJECTED);

		assertThat(bookingService.getAllBookingsByBooker(booker.getId(), "ALL", 0, 10)).hasSize(4);
		assertThat(bookingService.getAllBookingsByBooker(booker.getId(), "CURRENT", 0, 10)).hasSize(2);
		assertThat(bookingService.getAllBookingsByBooker(booker.getId(), "PAST", 0, 10)).hasSize(1);
		assertThat(bookingService.getAllBookingsByBooker(booker.getId(), "FUTURE", 0, 10)).hasSize(1);
		assertThat(bookingService.getAllBookingsByBooker(booker.getId(), "WAITING", 0, 10)).hasSize(1);
		assertThat(bookingService.getAllBookingsByBooker(booker.getId(), "REJECTED", 0, 10)).hasSize(1);
		assertThat(bookingService.getAllBookingsByBooker(booker.getId(), "all", 0, 2)).hasSize(2);
	}

	@Test
	void shouldReturnBookingsByOwnerForAllStates() {
		UserDto owner = createUser("owner@example.com");
		UserDto booker = createUser("booker@example.com");
		User ownerEntity = userRepository.findById(owner.getId()).orElseThrow();
		User bookerEntity = userRepository.findById(booker.getId()).orElseThrow();
		LocalDateTime now = LocalDateTime.now();
		createBookingForState(ownerEntity, bookerEntity, now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);
		createBookingForState(ownerEntity, bookerEntity, now.minusHours(1), now.plusHours(1), BookingStatus.APPROVED);
		createBookingForState(ownerEntity, bookerEntity, now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
		createBookingForState(ownerEntity, bookerEntity, now.minusDays(1), now.plusDays(1), BookingStatus.REJECTED);

		assertThat(bookingService.getAllBookingsByOwner(owner.getId(), "ALL", 0, 10)).hasSize(4);
		assertThat(bookingService.getAllBookingsByOwner(owner.getId(), "CURRENT", 0, 10)).hasSize(2);
		assertThat(bookingService.getAllBookingsByOwner(owner.getId(), "PAST", 0, 10)).hasSize(1);
		assertThat(bookingService.getAllBookingsByOwner(owner.getId(), "FUTURE", 0, 10)).hasSize(1);
		assertThat(bookingService.getAllBookingsByOwner(owner.getId(), "WAITING", 0, 10)).hasSize(1);
		assertThat(bookingService.getAllBookingsByOwner(owner.getId(), "REJECTED", 0, 10)).hasSize(1);
	}

	@Test
	void shouldValidateStateAndPagination() {
		UserDto owner = createUser("owner@example.com");

		assertThatThrownBy(() -> bookingService.getAllBookingsByBooker(owner.getId(), "UNKNOWN", 0, 10))
				.isInstanceOf(ValidationException.class);
		assertThatThrownBy(() -> bookingService.getAllBookingsByOwner(owner.getId(), "UNKNOWN", 0, 10))
				.isInstanceOf(ValidationException.class);
		assertThatThrownBy(() -> bookingService.getAllBookingsByBooker(owner.getId(), "ALL", -1, 10))
				.isInstanceOf(ValidationException.class);
		assertThatThrownBy(() -> bookingService.getAllBookingsByOwner(owner.getId(), "ALL", 0, 0))
				.isInstanceOf(ValidationException.class);
		assertThatThrownBy(() -> bookingService.getAllBookingsByBooker(999L, "ALL", 0, 10))
				.isInstanceOf(NotFoundException.class);
		assertThatThrownBy(() -> bookingService.getAllBookingsByOwner(999L, "ALL", 0, 10))
				.isInstanceOf(NotFoundException.class);
	}

	private UserDto createUser(String email) {
		return userService.addUser(new NewUserRequest("Пользователь", email));
	}

	private ItemDto createItem(Long ownerId) {
		return itemService.addItem(ownerId,
				new ItemDto(null, "Дрель", "Мощная дрель", true, null));
	}

	private BookingDto createBooking(Long bookerId, Long itemId) {
		return bookingService.addBooking(bookerId,
				new BookingInputDto(itemId, LocalDateTime.now().plusDays(1),
						LocalDateTime.now().plusDays(2)));
	}

	private Booking createBookingForState(User owner, User booker, LocalDateTime start,
										  LocalDateTime end, BookingStatus status) {
		Item item = itemRepository.save(new Item(
				null, "Вещь", "Описание", true, owner, null
		));
		return bookingRepository.save(new Booking(null, start, end, item, booker, status));
	}
}

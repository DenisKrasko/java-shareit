package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
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
class ItemRequestServiceIntegrationTest {
	@Autowired
	private ItemRequestService itemRequestService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRequestRepository itemRequestRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Test
	void shouldCreateRequest() {
		UserDto user = createUser("user@example.com");

		ItemRequestDto request = itemRequestService.addRequest(user.getId(),
				new NewItemRequestDto("Нужна дрель"));

		assertThat(request.getId()).isNotNull();
		assertThat(request.getDescription()).isEqualTo("Нужна дрель");
		assertThat(request.getCreated()).isNotNull();
		assertThat(request.getItems()).isEmpty();
	}

	@Test
	void shouldNotCreateRequestWithBlankDescription() {
		UserDto user = createUser("user@example.com");

		assertThatThrownBy(() -> itemRequestService.addRequest(user.getId(),
				new NewItemRequestDto(" ")))
				.isInstanceOf(ValidationException.class);
	}

	@Test
	void shouldNotCreateRequestForMissingUser() {
		assertThatThrownBy(() -> itemRequestService.addRequest(999L,
				new NewItemRequestDto("Нужна дрель")))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void shouldReturnUserRequestsSortedByCreatedDesc() {
		UserDto requestor = createUser("requestor@example.com");
		User requestorEntity = userRepository.findById(requestor.getId()).orElseThrow();
		UserDto owner = createUser("owner@example.com");
		User ownerEntity = userRepository.findById(owner.getId()).orElseThrow();
		ItemRequest older = itemRequestRepository.save(new ItemRequest(
				null, "Старый запрос", requestorEntity, LocalDateTime.now().minusDays(2)));
		ItemRequest newer = itemRequestRepository.save(new ItemRequest(
				null, "Новый запрос", requestorEntity, LocalDateTime.now().minusDays(1)));
		itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, ownerEntity, newer));

		List<ItemRequestDto> requests = itemRequestService.getUserRequests(requestor.getId());

		assertThat(requests).hasSize(2);
		assertThat(requests.get(0).getId()).isEqualTo(newer.getId());
		assertThat(requests.get(1).getId()).isEqualTo(older.getId());
		assertThat(requests.get(0).getItems()).hasSize(1);
		assertThat(requests.get(0).getItems().get(0).getName()).isEqualTo("Дрель");
		assertThat(requests.get(0).getItems().get(0).getOwnerId()).isEqualTo(owner.getId());
	}

	@Test
	void shouldReturnEmptyListWhenUserHasNoRequests() {
		UserDto user = createUser("user@example.com");

		assertThat(itemRequestService.getUserRequests(user.getId())).isEmpty();
	}

	@Test
	void shouldReturnAllRequestsOfOtherUsersWithPagination() {
		UserDto user = createUser("user@example.com");
		UserDto other = createUser("other@example.com");
		User otherEntity = userRepository.findById(other.getId()).orElseThrow();
		itemRequestRepository.save(new ItemRequest(
				null, "Запрос 1", otherEntity, LocalDateTime.now().minusDays(3)));
		itemRequestRepository.save(new ItemRequest(
				null, "Запрос 2", otherEntity, LocalDateTime.now().minusDays(2)));
		itemRequestRepository.save(new ItemRequest(
				null, "Запрос 3", otherEntity, LocalDateTime.now().minusDays(1)));
		User userEntity = userRepository.findById(user.getId()).orElseThrow();
		itemRequestRepository.save(new ItemRequest(
				null, "Мой запрос", userEntity, LocalDateTime.now()));

		List<ItemRequestDto> firstPage = itemRequestService.getAllRequests(user.getId(), 0, 2);
		List<ItemRequestDto> secondPage = itemRequestService.getAllRequests(user.getId(), 2, 2);

		assertThat(firstPage).hasSize(2);
		assertThat(secondPage).hasSize(1);
		assertThat(firstPage.get(0).getDescription()).isEqualTo("Запрос 3");
		assertThat(secondPage.get(0).getDescription()).isEqualTo("Запрос 1");
	}

	@Test
	void shouldReturnRequestByIdForAnyUser() {
		UserDto requestor = createUser("requestor@example.com");
		UserDto owner = createUser("owner@example.com");
		UserDto anyUser = createUser("any@example.com");
		User requestorEntity = userRepository.findById(requestor.getId()).orElseThrow();
		User ownerEntity = userRepository.findById(owner.getId()).orElseThrow();
		ItemRequest request = itemRequestRepository.save(new ItemRequest(
				null, "Нужна дрель", requestorEntity, LocalDateTime.now()));
		itemRepository.save(new Item(null, "Дрель", "Мощная дрель", true, ownerEntity, request));

		ItemRequestDto result = itemRequestService.getRequestById(anyUser.getId(), request.getId());

		assertThat(result.getId()).isEqualTo(request.getId());
		assertThat(result.getItems()).hasSize(1);
	}

	@Test
	void shouldThrowWhenRequestNotFound() {
		UserDto user = createUser("user@example.com");

		assertThatThrownBy(() -> itemRequestService.getRequestById(user.getId(), 999L))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void shouldValidatePaginationAndUser() {
		UserDto user = createUser("user@example.com");

		assertThatThrownBy(() -> itemRequestService.getAllRequests(user.getId(), -1, 10))
				.isInstanceOf(ValidationException.class);
		assertThatThrownBy(() -> itemRequestService.getAllRequests(user.getId(), 0, 0))
				.isInstanceOf(ValidationException.class);
		assertThatThrownBy(() -> itemRequestService.getAllRequests(999L, 0, 10))
				.isInstanceOf(NotFoundException.class);
		assertThatThrownBy(() -> itemRequestService.getUserRequests(999L))
				.isInstanceOf(NotFoundException.class);
		assertThatThrownBy(() -> itemRequestService.getRequestById(999L, 1L))
				.isInstanceOf(NotFoundException.class);
	}

	private UserDto createUser(String email) {
		return userService.addUser(new NewUserRequest("Пользователь", email));
	}
}

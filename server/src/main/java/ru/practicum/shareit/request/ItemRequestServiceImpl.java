package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
	private static final Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created")
			.and(Sort.by(Sort.Direction.DESC, "id"));

	private final ItemRequestRepository itemRequestRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;

	@Override
	@Transactional
	public ItemRequestDto addRequest(Long userId, NewItemRequestDto dto) {
		User requestor = getUser(userId);
		if (dto.getDescription() == null || dto.getDescription().isBlank()) {
			throw new ValidationException("описание запроса пустое");
		}
		ItemRequest request = new ItemRequest(
				null,
				dto.getDescription(),
				requestor,
				LocalDateTime.now()
		);
		ItemRequest savedRequest = itemRequestRepository.save(request);
		return ItemRequestMapper.toItemRequestDto(savedRequest, List.of());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequestDto> getUserRequests(Long userId) {
		getUser(userId);
		List<ItemRequest> requests = itemRequestRepository
				.findAllByRequestorIdOrderByCreatedDescIdDesc(userId);
		return toItemRequestDtos(requests);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
		getUser(userId);
		Pageable pageable = createPageable(from, size);
		List<ItemRequest> requests = itemRequestRepository
				.findAllByRequestorIdNot(userId, pageable)
				.getContent();
		return toItemRequestDtos(requests);
	}

	@Override
	@Transactional(readOnly = true)
	public ItemRequestDto getRequestById(Long userId, Long requestId) {
		getUser(userId);
		ItemRequest request = itemRequestRepository.findById(requestId)
				.orElseThrow(() -> new NotFoundException("запрос с id " + requestId + " не найден"));
		return toItemRequestDtos(List.of(request)).get(0);
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("пользователь с id " + userId + " не найден"));
	}

	private Pageable createPageable(int from, int size) {
		if (from < 0) {
			throw new ValidationException("параметр from не может быть отрицательным");
		}
		if (size < 1) {
			throw new ValidationException("параметр size должен быть больше нуля");
		}
		return PageRequest.of(from / size, size, CREATED_DESC);
	}

	private List<ItemRequestDto> toItemRequestDtos(List<ItemRequest> requests) {
		if (requests.isEmpty()) {
			return List.of();
		}
		List<Long> requestIds = requests.stream()
				.map(ItemRequest::getId)
				.collect(Collectors.toList());
		Map<Long, List<ItemRequestItemDto>> itemsByRequestId = itemRepository
				.findAllByRequestIds(requestIds)
				.stream()
				.collect(Collectors.groupingBy(
						item -> item.getRequest().getId(),
						Collectors.mapping(this::toItemRequestItemDto, Collectors.toList())
				));
		return requests.stream()
				.map(request -> ItemRequestMapper.toItemRequestDto(
						request,
						itemsByRequestId.getOrDefault(request.getId(), List.of())
				))
				.collect(Collectors.toList());
	}

	private ItemRequestItemDto toItemRequestItemDto(Item item) {
		return new ItemRequestItemDto(
				item.getId(),
				item.getName(),
				item.getOwner().getId()
		);
	}
}

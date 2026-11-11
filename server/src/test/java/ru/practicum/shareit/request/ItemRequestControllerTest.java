package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

	private ItemRequestService itemRequestService;
	private ItemRequestController itemRequestController;

	@BeforeEach
	void setUp() {
		itemRequestService = Mockito.mock(ItemRequestService.class);
		itemRequestController = new ItemRequestController(itemRequestService);
	}

	@Test
	void shouldCreateRequest() {
		NewItemRequestDto request = new NewItemRequestDto("нужна дрель");
		ItemRequestDto expected = requestDto();
		Mockito.when(itemRequestService.addRequest(1L, request)).thenReturn(expected);

		assertThat(itemRequestController.addRequest(1L, request)).isEqualTo(expected);
		Mockito.verify(itemRequestService).addRequest(1L, request);
	}

	@Test
	void shouldGetUserRequests() {
		List<ItemRequestDto> expected = List.of(requestDto());
		Mockito.when(itemRequestService.getUserRequests(1L)).thenReturn(expected);

		assertThat(itemRequestController.getUserRequests(1L)).isEqualTo(expected);
		Mockito.verify(itemRequestService).getUserRequests(1L);
	}

	@Test
	void shouldGetAllRequests() {
		List<ItemRequestDto> expected = List.of(requestDto());
		Mockito.when(itemRequestService.getAllRequests(1L, 0, 10)).thenReturn(expected);

		assertThat(itemRequestController.getAllRequests(1L, 0, 10)).isEqualTo(expected);
		Mockito.verify(itemRequestService).getAllRequests(1L, 0, 10);
	}

	@Test
	void shouldGetRequestById() {
		ItemRequestDto expected = requestDto();
		Mockito.when(itemRequestService.getRequestById(1L, 1L)).thenReturn(expected);

		assertThat(itemRequestController.getRequestById(1L, 1L)).isEqualTo(expected);
		Mockito.verify(itemRequestService).getRequestById(1L, 1L);
	}

	private ItemRequestDto requestDto() {
		return new ItemRequestDto(1L, "нужна дрель", LocalDateTime.now(), List.of());
	}
}

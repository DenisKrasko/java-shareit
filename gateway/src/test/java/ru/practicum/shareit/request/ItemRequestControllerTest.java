package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

	private ItemRequestClient itemRequestClient;
	private ItemRequestController itemRequestController;

	@BeforeEach
	void setUp() {
		itemRequestClient = Mockito.mock(ItemRequestClient.class);
		itemRequestController = new ItemRequestController(itemRequestClient);
	}

	@Test
	void shouldCreateRequest() {
		NewItemRequestDto request = new NewItemRequestDto("нужна дрель");
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1));
		Mockito.when(itemRequestClient.addRequest(1L, request)).thenReturn(expected);

		assertThat(itemRequestController.addRequest(1L, request)).isEqualTo(expected);
		Mockito.verify(itemRequestClient).addRequest(1L, request);
	}

	@Test
	void shouldGetUserRequests() {
		ResponseEntity<Object> expected = ResponseEntity.ok(List.of(Map.of("id", 1)));
		Mockito.when(itemRequestClient.getUserRequests(1L)).thenReturn(expected);

		assertThat(itemRequestController.getUserRequests(1L)).isEqualTo(expected);
		Mockito.verify(itemRequestClient).getUserRequests(1L);
	}

	@Test
	void shouldGetAllRequests() {
		ResponseEntity<Object> expected = ResponseEntity.ok(List.of(Map.of("id", 1)));
		Mockito.when(itemRequestClient.getAllRequests(1L, 0, 10)).thenReturn(expected);

		assertThat(itemRequestController.getAllRequests(1L, 0, 10)).isEqualTo(expected);
		Mockito.verify(itemRequestClient).getAllRequests(1L, 0, 10);
	}

	@Test
	void shouldGetRequestById() {
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1));
		Mockito.when(itemRequestClient.getRequestById(1L, 1L)).thenReturn(expected);

		assertThat(itemRequestController.getRequestById(1L, 1L)).isEqualTo(expected);
		Mockito.verify(itemRequestClient).getRequestById(1L, 1L);
	}
}

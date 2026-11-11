package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

	private ItemClient itemClient;
	private ItemController itemController;

	@BeforeEach
	void setUp() {
		itemClient = Mockito.mock(ItemClient.class);
		itemController = new ItemController(itemClient);
	}

	@Test
	void shouldCreateItem() {
		ItemDto request = new ItemDto(null, "дрель", "простая дрель", true, null);
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 5));
		Mockito.when(itemClient.addItem(1L, request)).thenReturn(expected);

		assertThat(itemController.addItem(1L, request)).isEqualTo(expected);
		Mockito.verify(itemClient).addItem(1L, request);
	}

	@Test
	void shouldUpdateItem() {
		UpdateItemDto request = new UpdateItemDto(null, "новая дрель", null, false, null);
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("name", "новая дрель"));
		Mockito.when(itemClient.updateItem(1L, 5L, request)).thenReturn(expected);

		assertThat(itemController.update(1L, 5L, request)).isEqualTo(expected);
		Mockito.verify(itemClient).updateItem(1L, 5L, request);
	}

	@Test
	void shouldGetItemById() {
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 5));
		Mockito.when(itemClient.getItem(1L, 5L)).thenReturn(expected);

		assertThat(itemController.getItemById(1L, 5L)).isEqualTo(expected);
		Mockito.verify(itemClient).getItem(1L, 5L);
	}

	@Test
	void shouldGetItemsByOwner() {
		ResponseEntity<Object> expected = ResponseEntity.ok(List.of(Map.of("id", 5)));
		Mockito.when(itemClient.getItems(1L)).thenReturn(expected);

		assertThat(itemController.getItemsByOwner(1L)).isEqualTo(expected);
		Mockito.verify(itemClient).getItems(1L);
	}

	@Test
	void shouldSearchItems() {
		ResponseEntity<Object> expected = ResponseEntity.ok(List.of(Map.of("name", "дрель")));
		Mockito.when(itemClient.searchItems(1L, "дрель")).thenReturn(expected);

		assertThat(itemController.searchItems(1L, "дрель")).isEqualTo(expected);
		Mockito.verify(itemClient).searchItems(1L, "дрель");
	}

	@Test
	void shouldAddComment() {
		CommentRequestDto request = new CommentRequestDto("хорошая вещь");
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 3));
		Mockito.when(itemClient.addComment(1L, 5L, request)).thenReturn(expected);

		assertThat(itemController.addComment(1L, 5L, request)).isEqualTo(expected);
		Mockito.verify(itemClient).addComment(1L, 5L, request);
	}
}

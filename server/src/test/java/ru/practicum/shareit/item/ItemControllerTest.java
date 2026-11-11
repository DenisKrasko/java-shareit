package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

	private ItemService itemService;
	private ItemController itemController;

	@BeforeEach
	void setUp() {
		itemService = Mockito.mock(ItemService.class);
		itemController = new ItemController(itemService);
	}

	@Test
	void shouldCreateItem() {
		ItemDto request = new ItemDto(null, "дрель", "простая дрель", true, null);
		ItemDto expected = new ItemDto(5L, "дрель", "простая дрель", true, null);
		Mockito.when(itemService.addItem(1L, request)).thenReturn(expected);

		assertThat(itemController.addItem(1L, request)).isEqualTo(expected);
		Mockito.verify(itemService).addItem(1L, request);
	}

	@Test
	void shouldUpdateItem() {
		ItemDto request = new ItemDto(null, "новая дрель", null, false, null);
		ItemDto expected = new ItemDto(5L, "новая дрель", "простая дрель", false, null);
		Mockito.when(itemService.update(1L, 5L, request)).thenReturn(expected);

		assertThat(itemController.update(1L, 5L, request)).isEqualTo(expected);
		Mockito.verify(itemService).update(1L, 5L, request);
	}

	@Test
	void shouldGetItemById() {
		ItemWithBookingDto expected = new ItemWithBookingDto();
		expected.setId(5L);
		Mockito.when(itemService.getItemById(5L, 1L)).thenReturn(expected);

		assertThat(itemController.getItemById(1L, 5L)).isSameAs(expected);
		Mockito.verify(itemService).getItemById(5L, 1L);
	}

	@Test
	void shouldGetItemsByOwner() {
		ItemWithBookingDto item = new ItemWithBookingDto();
		item.setId(5L);
		List<ItemWithBookingDto> expected = List.of(item);
		Mockito.when(itemService.getItemsByOwner(1L)).thenReturn(expected);

		assertThat(itemController.getItemsByOwner(1L)).isEqualTo(expected);
		Mockito.verify(itemService).getItemsByOwner(1L);
	}

	@Test
	void shouldSearchItems() {
		List<ItemDto> expected = List.of(new ItemDto(5L, "дрель", "простая дрель", true, null));
		Mockito.when(itemService.search("дрель")).thenReturn(expected);

		assertThat(itemController.searchItems("дрель")).isEqualTo(expected);
		Mockito.verify(itemService).search("дрель");
	}

	@Test
	void shouldAddComment() {
		CommentDto request = new CommentDto(null, "хорошая вещь", null, null);
		CommentDto expected = new CommentDto(2L, "хорошая вещь", "иван", LocalDateTime.now());
		Mockito.when(itemService.addComment(1L, 5L, request)).thenReturn(expected);

		assertThat(itemController.addComment(1L, 5L, request)).isEqualTo(expected);
		Mockito.verify(itemService).addComment(1L, 5L, request);
	}
}

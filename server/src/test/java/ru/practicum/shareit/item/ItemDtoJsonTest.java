package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldConvertItemDtoToJsonAndBack() throws Exception {
		ItemDto dto = new ItemDto(5L, "Дрель", "Мощная дрель", true, 7L);

		String json = objectMapper.writeValueAsString(dto);
		ItemDto result = objectMapper.readValue(json, ItemDto.class);

		assertThat(result).isEqualTo(dto);
	}

	@Test
	void shouldConvertItemWithBookingsAndCommentsToJsonAndBack() throws Exception {
		ItemWithBookingDto dto = new ItemWithBookingDto();
		dto.setId(5L);
		dto.setName("Дрель");
		dto.setDescription("Мощная дрель");
		dto.setAvailable(true);
		dto.setRequestId(7L);
		dto.setComments(List.of(new CommentDto(
				3L,
				"Отличная вещь",
				"Иван",
				LocalDateTime.of(2026, 1, 10, 10, 0, 30)
		)));

		String json = objectMapper.writeValueAsString(dto);
		ItemWithBookingDto result = objectMapper.readValue(json, ItemWithBookingDto.class);

		assertThat(result).isEqualTo(dto);
	}
}

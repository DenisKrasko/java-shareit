package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldConvertItemRequestDtoToJsonAndBack() throws Exception {
		ItemRequestDto dto = new ItemRequestDto(
				1L,
				"Нужна дрель",
				LocalDateTime.of(2026, 1, 10, 10, 0, 30),
				List.of(new ItemRequestItemDto(5L, "Дрель", 2L))
		);

		String json = objectMapper.writeValueAsString(dto);
		ItemRequestDto result = objectMapper.readValue(json, ItemRequestDto.class);

		assertThat(result).isEqualTo(dto);
	}

	@Test
	void shouldConvertNewItemRequestDtoToJsonAndBack() throws Exception {
		NewItemRequestDto dto = new NewItemRequestDto("Нужна дрель");

		String json = objectMapper.writeValueAsString(dto);
		NewItemRequestDto result = objectMapper.readValue(json, NewItemRequestDto.class);

		assertThat(result).isEqualTo(dto);
	}
}

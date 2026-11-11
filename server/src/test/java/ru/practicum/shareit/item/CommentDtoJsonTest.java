package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldConvertCommentDtoToJsonAndBack() throws Exception {
		CommentDto dto = new CommentDto(
				3L,
				"Отличная вещь",
				"Иван",
				LocalDateTime.of(2026, 1, 10, 10, 0, 30)
		);

		String json = objectMapper.writeValueAsString(dto);
		CommentDto result = objectMapper.readValue(json, CommentDto.class);

		assertThat(result).isEqualTo(dto);
	}
}

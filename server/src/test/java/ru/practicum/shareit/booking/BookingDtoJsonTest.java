package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.ItemBookingInfoDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldConvertBookingDtoToJsonAndBack() throws Exception {
		BookingDto dto = new BookingDto(
				1L,
				LocalDateTime.of(2026, 1, 10, 10, 0, 30),
				LocalDateTime.of(2026, 1, 11, 10, 0, 30),
				new ItemBookingInfoDto(5L, "Дрель"),
				new UserDto(2L, "Иван", "ivan@example.com"),
				BookingStatus.WAITING
		);

		String json = objectMapper.writeValueAsString(dto);
		BookingDto result = objectMapper.readValue(json, BookingDto.class);

		assertThat(result).isEqualTo(dto);
	}

	@Test
	void shouldConvertBookingInputDtoToJsonAndBack() throws Exception {
		BookingInputDto dto = new BookingInputDto(
				5L,
				LocalDateTime.of(2026, 1, 10, 10, 0, 30),
				LocalDateTime.of(2026, 1, 11, 10, 0, 30)
		);

		String json = objectMapper.writeValueAsString(dto);
		BookingInputDto result = objectMapper.readValue(json, BookingInputDto.class);

		assertThat(result).isEqualTo(dto);
	}
}

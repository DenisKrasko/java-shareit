package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingInputDto {

	@NotNull(message = "id вещи обязателен")
	private Long itemId;

	@NotNull(message = "дата начала не может быть пустой")
	private LocalDateTime start;

	@NotNull(message = "дата окончания не может быть пустой")
	private LocalDateTime end;
}

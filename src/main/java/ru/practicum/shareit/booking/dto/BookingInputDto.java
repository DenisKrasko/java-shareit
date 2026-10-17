package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingInputDto {

	@NotNull(message = "Идентификатор вещи обязателен")
	private Long itemId;

	@NotNull(message = "Дата начала не может быть пустой")
	@FutureOrPresent(message = "Дата начала не может быть в прошлом")
	private LocalDateTime start;

	@NotNull(message = "Дата окончания не может быть пустой")
	@Future(message = "Дата окончания должна быть в будущем")
	private LocalDateTime end;
}

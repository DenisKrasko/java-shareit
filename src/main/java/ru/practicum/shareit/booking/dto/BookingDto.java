package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class BookingDto {
	private LocalDateTime start;
	private LocalDateTime end;
	private Item item;
	private User booker;
	private BookingStatus status;
}

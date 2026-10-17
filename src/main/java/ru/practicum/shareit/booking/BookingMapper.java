package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.ItemBookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
	public static BookingDto toDto(Booking booking) {
		return new BookingDto(
				booking.getId(),
				booking.getStart(),
				booking.getEnd(),
				new ItemBookingInfoDto(booking.getItem().getId(), booking.getItem().getName()),
				new BookerDto(booking.getBooker().getId()),
				booking.getStatus()
		);
	}

	public static BookingShortDto toBookingShortDto(Booking booking) {
		if (booking == null) {
			return null;
		}
		return new BookingShortDto(
				booking.getId(),
				booking.getBooker().getId()
		);
	}
}

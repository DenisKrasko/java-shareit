package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

public interface BookingService {

	BookingDto addBooking(Long userId, BookingInputDto bookingInputDto);

	BookingDto approveBooking(Long userId, Long bookingId, Boolean approved);

	BookingDto getBookingById(Long userId, Long bookingId);

	List<BookingDto> getAllBookingsByBooker(Long userId, String state);

	List<BookingDto> getAllBookingsByOwner(Long userId, String state);

}

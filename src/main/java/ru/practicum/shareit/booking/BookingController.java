package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
	public static final String USER_ID = "X-Sharer-User-Id";

	private final BookingService bookingService;

	@PostMapping
	public BookingDto addBooking(@RequestHeader(USER_ID) Long userId,
								 @Valid @RequestBody BookingInputDto bookingInputDto) {
		return bookingService.addBooking(userId, bookingInputDto);
	}

	@PatchMapping("/{bookingId}")
	public BookingDto approveBooking(@RequestHeader(USER_ID) Long userId,
									 @PathVariable Long bookingId,
									 @RequestParam Boolean approved) {
		return bookingService.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public BookingDto getBookingById(@RequestHeader(USER_ID) Long userId,
									 @PathVariable Long bookingId) {
		return bookingService.getBookingById(userId, bookingId);
	}

	@GetMapping({"/", ""})
	public List<BookingDto> getAllBookingsByBooker(@RequestHeader(USER_ID) Long userId,
												   @RequestParam(defaultValue = "ALL") String state) {
		return bookingService.getAllBookingsByBooker(userId, state);
	}

	@GetMapping("/owner")
	public List<BookingDto> getAllBookingsByOwner(@RequestHeader(USER_ID) Long userId,
												  @RequestParam(defaultValue = "ALL") String state) {
		return bookingService.getAllBookingsByOwner(userId, state);
	}
}

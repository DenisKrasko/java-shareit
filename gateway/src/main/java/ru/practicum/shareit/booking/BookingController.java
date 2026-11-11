package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingState;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
	public static final String USER_ID = "X-Sharer-User-Id";

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) long userId,
											 @Valid @RequestBody BookingInputDto bookingInputDto) {
		validateDates(bookingInputDto);
		return bookingClient.addBooking(userId, bookingInputDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID) long userId,
												 @PathVariable Long bookingId,
												 @RequestParam Boolean approved) {
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID) long userId,
												 @PathVariable Long bookingId) {
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping({"/", ""})
	public ResponseEntity<Object> getAllBookingsByBooker(
			@RequestHeader(USER_ID) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = parseState(stateParam);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsByOwner(
			@RequestHeader(USER_ID) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = parseState(stateParam);
		return bookingClient.getOwnerBookings(userId, state, from, size);
	}

	private BookingState parseState(String stateParam) {
		return BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
	}

	private void validateDates(BookingInputDto bookingInputDto) {
		if (bookingInputDto.getStart() != null && bookingInputDto.getEnd() != null
				&& !bookingInputDto.getEnd().isAfter(bookingInputDto.getStart())) {
			throw new IllegalArgumentException("дата окончания должна быть позже даты начала");
		}
	}
}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

	private BookingClient bookingClient;
	private BookingController bookingController;

	@BeforeEach
	void setUp() {
		bookingClient = Mockito.mock(BookingClient.class);
		bookingController = new BookingController(bookingClient);
	}

	@Test
	void shouldCreateBooking() {
		BookingInputDto request = new BookingInputDto(
				5L,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2)
		);
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1));
		Mockito.when(bookingClient.addBooking(1L, request)).thenReturn(expected);

		assertThat(bookingController.addBooking(1L, request)).isEqualTo(expected);
		Mockito.verify(bookingClient).addBooking(1L, request);
	}

	@Test
	void shouldApproveBooking() {
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("status", "APPROVED"));
		Mockito.when(bookingClient.approveBooking(1L, 1L, true)).thenReturn(expected);

		assertThat(bookingController.approveBooking(1L, 1L, true)).isEqualTo(expected);
		Mockito.verify(bookingClient).approveBooking(1L, 1L, true);
	}

	@Test
	void shouldGetBookingById() {
		ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 1));
		Mockito.when(bookingClient.getBooking(1L, 1L)).thenReturn(expected);

		assertThat(bookingController.getBookingById(1L, 1L)).isEqualTo(expected);
		Mockito.verify(bookingClient).getBooking(1L, 1L);
	}

	@Test
	void shouldGetBookingsByBooker() {
		ResponseEntity<Object> expected = ResponseEntity.ok(List.of(Map.of("id", 1)));
		Mockito.when(bookingClient.getBookings(1L, BookingState.ALL, 0, 10)).thenReturn(expected);

		assertThat(bookingController.getAllBookingsByBooker(1L, "all", 0, 10)).isEqualTo(expected);
		Mockito.verify(bookingClient).getBookings(1L, BookingState.ALL, 0, 10);
	}

	@Test
	void shouldGetBookingsByOwner() {
		ResponseEntity<Object> expected = ResponseEntity.ok(List.of(Map.of("id", 1)));
		Mockito.when(bookingClient.getOwnerBookings(1L, BookingState.ALL, 0, 10)).thenReturn(expected);

		assertThat(bookingController.getAllBookingsByOwner(1L, "ALL", 0, 10)).isEqualTo(expected);
		Mockito.verify(bookingClient).getOwnerBookings(1L, BookingState.ALL, 0, 10);
	}
}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

	private BookingService bookingService;
	private BookingController bookingController;

	@BeforeEach
	void setUp() {
		bookingService = Mockito.mock(BookingService.class);
		bookingController = new BookingController(bookingService);
	}

	@Test
	void shouldCreateBooking() {
		BookingInputDto request = new BookingInputDto(
				5L,
				LocalDateTime.now().plusDays(1),
				LocalDateTime.now().plusDays(2)
		);
		BookingDto expected = booking();
		Mockito.when(bookingService.addBooking(1L, request)).thenReturn(expected);

		assertThat(bookingController.addBooking(1L, request)).isEqualTo(expected);
		Mockito.verify(bookingService).addBooking(1L, request);
	}

	@Test
	void shouldApproveBooking() {
		BookingDto expected = booking();
		expected.setStatus(BookingStatus.APPROVED);
		Mockito.when(bookingService.approveBooking(1L, 1L, true)).thenReturn(expected);

		assertThat(bookingController.approveBooking(1L, 1L, true)).isEqualTo(expected);
		Mockito.verify(bookingService).approveBooking(1L, 1L, true);
	}

	@Test
	void shouldGetBookingById() {
		BookingDto expected = booking();
		Mockito.when(bookingService.getBookingById(1L, 1L)).thenReturn(expected);

		assertThat(bookingController.getBookingById(1L, 1L)).isEqualTo(expected);
		Mockito.verify(bookingService).getBookingById(1L, 1L);
	}

	@Test
	void shouldGetBookingsByBooker() {
		List<BookingDto> expected = List.of(booking());
		Mockito.when(bookingService.getAllBookingsByBooker(1L, "ALL", 0, 10)).thenReturn(expected);

		assertThat(bookingController.getAllBookingsByBooker(1L, "ALL", 0, 10)).isEqualTo(expected);
		Mockito.verify(bookingService).getAllBookingsByBooker(1L, "ALL", 0, 10);
	}

	@Test
	void shouldGetBookingsByOwner() {
		List<BookingDto> expected = List.of(booking());
		Mockito.when(bookingService.getAllBookingsByOwner(1L, "ALL", 0, 10)).thenReturn(expected);

		assertThat(bookingController.getAllBookingsByOwner(1L, "ALL", 0, 10)).isEqualTo(expected);
		Mockito.verify(bookingService).getAllBookingsByOwner(1L, "ALL", 0, 10);
	}

	private BookingDto booking() {
		BookingDto booking = new BookingDto();
		booking.setId(1L);
		booking.setStatus(BookingStatus.WAITING);
		return booking;
	}
}

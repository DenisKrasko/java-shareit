package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final ItemRepository itemRepository;
	private final UserRepository userRepository;

	@Transactional
	public BookingDto addBooking(Long userId, BookingInputDto dto) {
		User booker = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
		Item item = itemRepository.findById(dto.getItemId())
				.orElseThrow(() -> new NotFoundException("Вещь с id " + dto.getItemId() + " не найдена"));
		validateBookingCreation(item, userId);
		validateBookingDates(dto);
		Booking booking = new Booking(
				null,
				dto.getStart(),
				dto.getEnd(),
				item,
				booker,
				BookingStatus.WAITING
		);
		Booking savedBooking = bookingRepository.save(booking);
		return BookingMapper.toDto(savedBooking);
	}

	@Transactional
	public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
		validateBookingApprove(booking, userId);
		if (approved) {
			booking.setStatus(BookingStatus.APPROVED);
		} else {
			booking.setStatus(BookingStatus.REJECTED);
		}
		return BookingMapper.toDto(bookingRepository.save(booking));
	}

	public BookingDto getBookingById(Long userId, Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
		validateAccess(booking, userId);
		return BookingMapper.toDto(booking);
	}

	public List<BookingDto> getAllBookingsByBooker(Long userId, String stateStr) {

		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь не найден"));
		BookingState state;
		try {
			state = BookingState.valueOf(stateStr.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new ValidationException("Неизвестное состояние: " + stateStr);
		}
		LocalDateTime now = LocalDateTime.now();
		Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
		List<Booking> bookings;
		switch (state) {
			case ALL:
				bookings = bookingRepository.findByBooker_Id(userId, sortByStartDesc);
				break;
			case CURRENT:
				bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, sortByStartDesc);
				break;
			case PAST:
				bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, sortByStartDesc);
				break;
			case FUTURE:
				bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, sortByStartDesc);
				break;
			case WAITING:
				bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
				break;
			case REJECTED:
				bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
				break;
			default:
				bookings = Collections.emptyList();
		}
		return bookings.stream()
				.map(BookingMapper::toDto)
				.collect(Collectors.toList());
	}

	public List<BookingDto> getAllBookingsByOwner(Long userId, String stateStr) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь не найден"));
		BookingState state;
		try {
			state = BookingState.valueOf(stateStr.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new ValidationException("Неизвестное состояние: " + stateStr);
		}
		LocalDateTime now = LocalDateTime.now();
		Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
		List<Booking> bookings;
		switch (state) {
			case ALL:
				bookings = bookingRepository.findByItem_Owner_Id(userId, sortByStartDesc);
				break;
			case PAST:
				bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(userId, now, sortByStartDesc);
				break;
			case FUTURE:
				bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(userId, now, sortByStartDesc);
				break;
			case CURRENT:
				bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(userId, now, now, sortByStartDesc);
				break;
			case WAITING:
				bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING, sortByStartDesc);
				break;
			case REJECTED:
				bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED, sortByStartDesc);
				break;
			default:
				bookings = Collections.emptyList();
		}
		return bookings.stream()
				.map(BookingMapper::toDto)
				.collect(Collectors.toList());
	}

	private void validateBookingCreation(Item item, Long userId) {
		if (item.getOwner().getId().equals(userId))
			throw new NotFoundException("Владелец не может забронировать свою же вещь");
		if (!item.isAvailable())
			throw new ValidationException("Вещь недоступна для бронирования");
	}

	private void validateBookingDates(BookingInputDto dto) {
		if (dto.getStart() == null || dto.getEnd() == null)
			throw new ValidationException("Даты начала и окончания бронирования обязательны");
		if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().equals(dto.getStart()))
			throw new ValidationException("Дата окончания должна быть позже даты начала");
	}

	private void validateBookingApprove(Booking booking, Long userId) {
		if (!booking.getItem().getOwner().getId().equals(userId))
			throw new AccessException("Изменять статус может только владелец вещи");
		if (booking.getStatus() != BookingStatus.WAITING)
			throw new ValidationException("Статус уже был изменен");
	}

	private void validateAccess(Booking booking, Long userId) {
		boolean isBooker = booking.getBooker().getId().equals(userId);
		boolean isOwner = booking.getItem().getOwner().getId().equals(userId);
		if (!isBooker && !isOwner)
			throw new NotFoundException("У вас нет прав на просмотр этого бронирования");
	}

}

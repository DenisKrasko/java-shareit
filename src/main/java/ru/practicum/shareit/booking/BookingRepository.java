package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	/**
	 * Для получения ВСЕХ бронирований пользователя (ALL)
	 */
	List<Booking> findByBooker_Id(Long bookerId, Sort sort);

	/**
	 * Для прошлых бронирований (PAST): дата окончания бронирования уже в прошлом
	 */
	List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

	/**
	 * Для будущих бронирований (FUTURE): дата начала бронирования еще впереди
	 */
	List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

	/**
	 * Для текущих бронирований (CURRENT): бронирование уже началось, но еще не закончилось
	 */
	List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

	/**
	 * Для фильтрации по конкретному статусу (WAITING или REJECTED)
	 */
	List<Booking> findByBooker_IdAndStatus(Long bookerId, BookingStatus status, Sort sort);

	/**
	 * Для получения всех бронирований вещей данного владельца (ALL)
	 */
	List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

	/**
	Для прошлых бронирований (PAST): аренда вещи уже завершилась
	 */
	List<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

	/**
	Для будущих бронирований (FUTURE): аренда вещи начнется в будущем
	 */
	List<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

	/**
	Для текущих бронирований (CURRENT): вещь находится в процессе аренды прямо сейчас
	 */
	List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

	/**
	Для фильтрации бронирований по конкретному статусу (WAITING или REJECTED)
	 */
	List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Sort sort);

	/**
	 * Находим все одобренные бронирования для конкретной вещи, отсортированные по времени начала
	 */
	List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus status, Sort sort);

	boolean existsByBooker_IdAndItemIdAndStatusAndEndIsBefore(
			Long bookerId, Long itemId, BookingStatus status, LocalDateTime end);

	@Query("""
        SELECT b
        FROM Booking AS b
        LEFT JOIN FETCH b.item
        LEFT JOIN FETCH b.booker
        WHERE b.item.owner.id = ?1
                AND b.status = 'APPROVED'
        ORDER BY b.start
        """)
	List<Booking> findApprovedWithItemAndBookerByOwnerId(Long ownerId);
}

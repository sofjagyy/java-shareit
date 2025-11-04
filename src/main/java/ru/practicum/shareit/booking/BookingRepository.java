package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCreatorId(Long creatorId, Sort sort);

    List<Booking> findByCreatorIdAndStatus(Long creatorId, BookingStatus status, Sort sort);

    List<Booking> findByCreatorIdAndEndIsBefore(Long creatorId, LocalDateTime end, Sort sort);

    List<Booking> findByCreatorIdAndStartIsAfter(Long creatorId, LocalDateTime start, Sort sort);

    List<Booking> findByCreatorIdAndStartIsBeforeAndEndIsAfter(Long creatorId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.status = 'APPROVED' AND b.start < ?2 ORDER BY b.start DESC")
    List<Booking> findLastBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.status = 'APPROVED' AND b.start > ?2 ORDER BY b.start ASC")
    List<Booking> findNextBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 AND b.status = 'APPROVED' AND b.start < ?2 ORDER BY b.start DESC")
    List<Booking> findLastBookingsForItems(List<Long> itemIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 AND b.status = 'APPROVED' AND b.start > ?2 ORDER BY b.start ASC")
    List<Booking> findNextBookingsForItems(List<Long> itemIds, LocalDateTime now);

    List<Booking> findByCreatorIdAndItemIdAndStatusAndEndIsBefore(Long creatorId, Long itemId, BookingStatus status, LocalDateTime end);
}


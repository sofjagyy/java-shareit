package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCreatorId(Long creatorId, Sort sort);

    List<Booking> findByCreatorIdAndStatus(Long creatorId, BookingStatus status, Sort sort);

    List<Booking> findByCreatorIdAndEndDateIsBefore(Long creatorId, LocalDateTime endDate, Sort sort);

    List<Booking> findByCreatorIdAndStartDateIsAfter(Long creatorId, LocalDateTime startDate, Sort sort);

    List<Booking> findByCreatorIdAndStartDateIsBeforeAndEndDateIsAfter(Long creatorId, LocalDateTime startDate, LocalDateTime endDate, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerIdAndEndDateIsBefore(Long ownerId, LocalDateTime endDate, Sort sort);

    List<Booking> findByItemOwnerIdAndStartDateIsAfter(Long ownerId, LocalDateTime startDate, Sort sort);

    List<Booking> findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(Long ownerId, LocalDateTime startDate, LocalDateTime endDate, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.status = 'APPROVED' AND b.startDate < ?2 ORDER BY b.startDate DESC")
    Optional<Booking> findLastBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.status = 'APPROVED' AND b.startDate > ?2 ORDER BY b.startDate ASC")
    Optional<Booking> findNextBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 AND b.status = 'APPROVED' AND b.startDate < ?2 ORDER BY b.startDate DESC")
    List<Booking> findLastBookingsForItems(List<Long> itemIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN ?1 AND b.status = 'APPROVED' AND b.startDate > ?2 ORDER BY b.startDate ASC")
    List<Booking> findNextBookingsForItems(List<Long> itemIds, LocalDateTime now);

    List<Booking> findByCreatorIdAndItemIdAndStatusAndEndDateIsBefore(Long creatorId, Long itemId, BookingStatus status, LocalDateTime endDate);
}


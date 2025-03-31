package com.backbenchcoders.innspiration.repository;

import com.backbenchcoders.innspiration.dto.RoomPriceDto;
import com.backbenchcoders.innspiration.entity.Hotel;
import com.backbenchcoders.innspiration.entity.Inventory;
import com.backbenchcoders.innspiration.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByRoom(Room room);

    @Query("""
           SELECT DISTINCT i.hotel
           FROM Inventory i
           WHERE i.city = :city
                AND i.date BETWEEN :startDate AND :endDate
                AND i.isClosed = false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
           GROUP BY i.hotel, i.room
           HAVING COUNT(i.date) = :dateCount
           """)
    Page<Hotel> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );


    @Query("""
           SELECT i
           FROM Inventory i
           WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND i.isClosed = false
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
           """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate AND :endDate
                AND (i.totalCount - i.bookedCount) >= :roomsCount
                AND i.isClosed = false
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.reservedCount = i.reservedCount + :roomsCount
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate and :endDate
                AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
                AND i.isClosed = false
    """)
    void initBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.reservedCount = i.reservedCount - :roomsCount,
                i.bookedCount = i.bookedCount + :roomsCount
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate and :endDate
                AND (i.totalCount - i.bookedCount) >= :roomsCount
                AND i.reservedCount >= :roomsCount
                AND i.isClosed = false
    """)
    void confirmBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.bookedCount = i.bookedCount - :roomsCount
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate and :endDate
                AND (i.totalCount - i.bookedCount) >= :roomsCount
                AND i.isClosed = false
    """)
    void cancelBooking(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount
    );

    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);

    List<Inventory> findByRoomOrderByDate(Room room);

    @Modifying
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate and :endDate
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> getInventoryAndLockBeforeUpdate(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.surgeFactor = :surgeFactor,
                i.isClosed = :isClosed
            WHERE i.room.id = :roomId
                AND i.date BETWEEN :startDate and :endDate
    """)
    void updateInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("surgeFactor") BigDecimal surgeFactor,
            @Param("isClosed") Boolean isClosed
    );

    @Query("""
       SELECT new com.backbenchcoders.innspiration.dto.RoomPriceDto(
            i.room,
            CASE
                WHEN COUNT(i) = :dateCount THEN AVG(i.price)
                ELSE NULL
            END
        )
       FROM Inventory i
       WHERE i.hotel.id = :hotelId
             AND i.date BETWEEN :startDate AND :endDate
             AND (i.totalCount - i.bookedCount) >= :roomsCount
             AND i.isClosed = false
       GROUP BY i.room
       """)
    List<RoomPriceDto> findRoomAveragePrice(
            @Param("hotelId") Long hotelId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Long roomsCount,
            @Param("dateCount") Long dateCount
    );
}

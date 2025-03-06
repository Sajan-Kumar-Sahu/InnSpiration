package com.backbenchcoders.innspiration.repository;

import com.backbenchcoders.innspiration.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
}

package com.backbenchcoders.innspiration.repository;

import com.backbenchcoders.innspiration.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}
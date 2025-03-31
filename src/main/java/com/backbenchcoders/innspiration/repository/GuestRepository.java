package com.backbenchcoders.innspiration.repository;

import com.backbenchcoders.innspiration.entity.Guest;
import com.backbenchcoders.innspiration.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByUser(User user);
}
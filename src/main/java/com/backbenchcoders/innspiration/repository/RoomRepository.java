package com.backbenchcoders.innspiration.repository;

import com.backbenchcoders.innspiration.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}

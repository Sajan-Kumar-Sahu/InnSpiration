package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.RoomDto;
import com.backbenchcoders.innspiration.entity.Room;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);

    List<RoomDto> getAllRoomsInHotel(Long hotelId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long roomId);
}

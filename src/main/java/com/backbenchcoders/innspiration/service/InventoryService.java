package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.HotelDto;
import com.backbenchcoders.innspiration.dto.HotelSearchRequest;
import com.backbenchcoders.innspiration.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);

}

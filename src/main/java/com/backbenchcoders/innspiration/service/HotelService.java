package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.HotelDto;
import com.backbenchcoders.innspiration.entity.Hotel;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);
}

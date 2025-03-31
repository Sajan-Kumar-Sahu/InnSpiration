package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.HotelDto;
import com.backbenchcoders.innspiration.dto.HotelInfoDto;
import com.backbenchcoders.innspiration.dto.HotelInfoRequestDto;
import com.backbenchcoders.innspiration.entity.Hotel;

import java.util.List;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId, HotelInfoRequestDto hotelInfoRequestDto);

    List<HotelDto> getAllHotels();
}

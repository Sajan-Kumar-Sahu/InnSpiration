package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.HotelDto;
import com.backbenchcoders.innspiration.entity.Hotel;
import com.backbenchcoders.innspiration.exception.ResourceNotFoundException;
import com.backbenchcoders.innspiration.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    public final ModelMapper modelMapper;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new Hotel with name: {}",hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setIsActive(false);
        hotelRepository.save(hotel);
        log.info("Hotel created with ID: {}", hotel.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Getting Hotel with ID: {}", id);
        Hotel hotel = hotelRepository
                      .findById(id)
                      .orElseThrow(()->new ResourceNotFoundException("Hotel Not Found with id: "+id));
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating Hotel with Id: {}",id);
        Hotel hotel = hotelRepository
                      .findById(id)
                      .orElseThrow(()->new ResourceNotFoundException("Hotel Not found with id: "+id));
        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public void deleteHotelById(Long id) {
        log.info("Deleting Hotel with ID: {}", id);
        boolean exists = hotelRepository.existsById(id);
        if(!exists){
            throw new ResourceNotFoundException("Hotel does not exist with id: "+id);
        }
        hotelRepository.deleteById(id);
        //TODO: delete the future inventory for this hotel.
    }

    @Override
    public void activateHotel(Long hotelId) {
        log.info("Activating Hotel with id: {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not Found with id: "+hotelId));

        hotel.setIsActive(true);
        //TODO: create inventory for the room of this hotel.

    }
}

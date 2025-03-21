package com.backbenchcoders.innspiration.service.implementation;

import com.backbenchcoders.innspiration.dto.HotelDto;
import com.backbenchcoders.innspiration.dto.HotelInfoDto;
import com.backbenchcoders.innspiration.dto.RoomDto;
import com.backbenchcoders.innspiration.entity.Hotel;
import com.backbenchcoders.innspiration.entity.Room;
import com.backbenchcoders.innspiration.entity.User;
import com.backbenchcoders.innspiration.exception.ResourceNotFoundException;
import com.backbenchcoders.innspiration.exception.UnauthorizedException;
import com.backbenchcoders.innspiration.repository.HotelRepository;
import com.backbenchcoders.innspiration.repository.RoomRepository;
import com.backbenchcoders.innspiration.service.HotelService;
import com.backbenchcoders.innspiration.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    public final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new Hotel with name: {}",hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setIsActive(false);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);

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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
           throw new UnauthorizedException("This user does not own this hotel with id: "+id);
        }

        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating Hotel with Id: {}",id);
        Hotel hotel = hotelRepository
                      .findById(id)
                      .orElseThrow(()->new ResourceNotFoundException("Hotel Not found with id: "+id));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("This user does not own this hotel with id: "+id);
        }

        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        log.info("Deleting Hotel with ID: {}", id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not Found with id: "+id));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("This user does not own this hotel with id: "+id);
        }

        for(Room room: hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(id);

    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating Hotel with id: {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not Found with id: "+hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnauthorizedException("This user does not own this hotel with id: "+hotelId);
        }

        hotel.setIsActive(true);
        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }
    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not Found with id: "+hotelId));

        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDto.class))
                .toList();

        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);
    }
}

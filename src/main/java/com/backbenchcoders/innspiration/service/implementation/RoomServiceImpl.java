package com.backbenchcoders.innspiration.service.implementation;

import com.backbenchcoders.innspiration.dto.RoomDto;
import com.backbenchcoders.innspiration.entity.Hotel;
import com.backbenchcoders.innspiration.entity.Room;
import com.backbenchcoders.innspiration.exception.ResourceNotFoundException;
import com.backbenchcoders.innspiration.repository.HotelRepository;
import com.backbenchcoders.innspiration.repository.RoomRepository;
import com.backbenchcoders.innspiration.service.InventoryService;
import com.backbenchcoders.innspiration.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("Creating Room for Hotel with ID:{}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not Found with ID: "+hotelId));
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room= roomRepository.save(room);

        if(hotel.getIsActive()){
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room,RoomDto.class);

    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all room in hotel with ID: {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not Found with ID: "+hotelId));

        return hotel.getRooms()
                .stream()
                .map((element)-> modelMapper.map(element,RoomDto.class)).collect(Collectors.toList());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Getting room with ID: {}",roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room Not found with ID: "+roomId));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Removing room with ID: {}",roomId);
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room Not found with ID: "+roomId));

        roomRepository.deleteById(roomId);

        inventoryService.deleteAllInventories(room);
    }
}

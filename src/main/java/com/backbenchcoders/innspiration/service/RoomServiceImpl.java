package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.RoomDto;
import com.backbenchcoders.innspiration.entity.Hotel;
import com.backbenchcoders.innspiration.entity.Room;
import com.backbenchcoders.innspiration.entity.User;
import com.backbenchcoders.innspiration.exception.ResourceNotFoundException;
import com.backbenchcoders.innspiration.exception.UnauthorizedException;
import com.backbenchcoders.innspiration.repository.HotelRepository;
import com.backbenchcoders.innspiration.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.backbenchcoders.innspiration.util.AppUtils.getCurrentUser;

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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(hotel.getOwner().getId())){
            throw new UnauthorizedException("This user does not own this hotel with  hotelId: "+hotelId);
        }

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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(hotel.getOwner().getId())){
            throw new UnauthorizedException("This user does not own this hotel with  hotelId: "+hotelId);
        }

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

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getId().equals(room.getHotel().getOwner().getId())){
            throw new UnauthorizedException("This user does not own this room with  hotelId: "+roomId);
        }

        roomRepository.deleteById(roomId);

        inventoryService.deleteAllInventories(room);
    }

    @Override
    @Transactional
    public RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto) {
        log.info("Updating Room with Id: {}", roomId);
        Hotel hotel = hotelRepository
                .findById( hotelId)
                .orElseThrow(()->new ResourceNotFoundException("Hotel Not found with  hotelId: "+ hotelId));

        User user = getCurrentUser();
        if(!user.getId().equals(hotel.getOwner().getId())){
            throw new UnauthorizedException("This user does not own this hotel with  hotelId: "+ hotelId);
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(()->new ResourceNotFoundException("Room Not found with  hotelId: "+ roomId));

        modelMapper.map(roomDto, room);
        room.setId(roomId);
        room = roomRepository.save(room);

        //TODO: if price or inventory is updated, then update the inventory for this room.

        return modelMapper.map(room, RoomDto.class);
    }
}

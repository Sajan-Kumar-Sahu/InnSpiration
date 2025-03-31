package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.*;
import com.backbenchcoders.innspiration.entity.Hotel;
import com.backbenchcoders.innspiration.entity.Room;
import com.backbenchcoders.innspiration.entity.User;
import com.backbenchcoders.innspiration.exception.ResourceNotFoundException;
import com.backbenchcoders.innspiration.exception.UnauthorizedException;
import com.backbenchcoders.innspiration.repository.HotelRepository;
import com.backbenchcoders.innspiration.repository.InventoryRepository;
import com.backbenchcoders.innspiration.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.backbenchcoders.innspiration.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    public final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Creating a new Hotel with name: {}",hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setIsActive(false);

        User user = getCurrentUser();
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

        User user = getCurrentUser();
        if(!user.getId().equals(hotel.getOwner().getId())){
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

        User user = getCurrentUser();
        if(!user.getId().equals(hotel.getOwner().getId())){
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

        User user = getCurrentUser();
        if(!user.getId().equals(hotel.getOwner().getId())){
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

        User user = getCurrentUser();
        if(!user.getId().equals(hotel.getOwner().getId())){
            throw new UnauthorizedException("This user does not own this hotel with id: "+hotelId);
        }

        hotel.setIsActive(true);
        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }
    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId, HotelInfoRequestDto hotelInfoRequestDto) {

        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        long daysCount = ChronoUnit.DAYS.between(hotelInfoRequestDto.getStartDate(), hotelInfoRequestDto.getEndDate())+1;

        List<RoomPriceDto> roomPriceDtoList = inventoryRepository.findRoomAveragePrice(hotelId,
                hotelInfoRequestDto.getStartDate(), hotelInfoRequestDto.getEndDate(),
                hotelInfoRequestDto.getRoomsCount(), daysCount);

        List<RoomPriceResponseDto> rooms = roomPriceDtoList.stream()
                .map(roomPriceDto -> {
                    RoomPriceResponseDto roomPriceResponseDto = modelMapper.map(roomPriceDto.getRoom(),
                            RoomPriceResponseDto.class);
                    roomPriceResponseDto.setPrice(roomPriceDto.getPrice());
                    return roomPriceResponseDto;
                })
                .collect(Collectors.toList());

        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), rooms);
    }

    @Override
    public List<HotelDto> getAllHotels() {
        User user = getCurrentUser();
        log.info("Getting all hotels for the admin user with ID: {}",user.getId());

        List<Hotel> hotels = hotelRepository.findByOwner(user);

        return hotels
                .stream()
                .map((element) -> modelMapper.map(element, HotelDto.class))
                .collect(Collectors.toList());
    }
}

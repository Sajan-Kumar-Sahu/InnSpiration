package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.*;
import com.backbenchcoders.innspiration.entity.Inventory;
import com.backbenchcoders.innspiration.entity.Room;
import com.backbenchcoders.innspiration.entity.User;
import com.backbenchcoders.innspiration.exception.ResourceNotFoundException;
import com.backbenchcoders.innspiration.repository.HotelMinPriceRepository;
import com.backbenchcoders.innspiration.repository.InventoryRepository;
import com.backbenchcoders.innspiration.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.backbenchcoders.innspiration.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final ModelMapper modelMapper;

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        for(;!today.isAfter(endDate);today=today.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .totalCount(room.getTotalCount())
                    .surgeFactor(BigDecimal.ONE)
                    .isClosed(false)
                    .build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteAllInventories(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceResponseDto> searchHotels(HotelSearchRequest hotelSearchRequest) {

        log.info("Searching hotels for {} city, from {} to {}", hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount =
                ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        // business logic - 90 days
        Page<HotelPriceDto> hotelPage =
                hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                        hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(), hotelSearchRequest.getRoomsCount(),
                        dateCount, pageable);

        return hotelPage.map(hotelPriceDto -> {
            HotelPriceResponseDto hotelPriceResponseDto = modelMapper.map(hotelPriceDto.getHotel(), HotelPriceResponseDto.class);
            hotelPriceResponseDto.setPrice(hotelPriceDto.getPrice());
            return hotelPriceResponseDto;
        });
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {
        log.info("Getting all Inventory by Room for room with ID: {}",roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room Not found with id: "+roomId));

        User user = getCurrentUser();
        if(!user.getId().equals(room.getHotel().getOwner().getId())){
            throw new AccessDeniedException("You are not the owner of the room with ID: "+roomId);
        }

        return inventoryRepository.findByRoomOrderByDate(room).stream()
                .map((element) -> modelMapper.map(element, InventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("Updating all Inventory by Room for room with ID: {} between date range: {} - {}",roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate()
        );
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room Not found with id: "+roomId));

        User user = getCurrentUser();
        if(!user.getId().equals(room.getHotel().getOwner().getId())){
            throw new AccessDeniedException("You are not the owner of the room with ID: "+roomId);
        }

        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate()
        );

        inventoryRepository.updateInventory(roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(),
                updateInventoryRequestDto.getSurgeFactor(),
                updateInventoryRequestDto.getIsClosed()
        );
    }
}

package com.backbenchcoders.innspiration.controller;

import com.backbenchcoders.innspiration.dto.*;
import com.backbenchcoders.innspiration.service.HotelService;
import com.backbenchcoders.innspiration.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceResponseDto>> searchHotels(@RequestParam String city,
                                                                    @RequestParam LocalDate startDate,
                                                                    @RequestParam LocalDate endDate,
                                                                    @RequestParam Integer roomsCount,
                                                                    @RequestParam(defaultValue = "0") Integer page,
                                                                    @RequestParam(defaultValue = "10") Integer size){
        HotelSearchRequest hotelSearchRequest = new HotelSearchRequest(city, startDate, endDate, roomsCount, page, size);
        var pageResult = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId,
                                                     @RequestParam LocalDate startDate,
                                                     @RequestParam LocalDate endDate,
                                                     @RequestParam Long roomsCount){
        HotelInfoRequestDto hotelInfoRequestDto = new HotelInfoRequestDto(startDate, endDate, roomsCount);
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId, hotelInfoRequestDto));
    }
}

package com.backbenchcoders.innspiration.controller;

import com.backbenchcoders.innspiration.dto.BookingDto;
import com.backbenchcoders.innspiration.dto.BookingRequest;
import com.backbenchcoders.innspiration.dto.GuestDto;
import com.backbenchcoders.innspiration.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initiateBooking(@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initiateBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@RequestBody List<GuestDto> guestDtoList, @PathVariable Long bookingId){
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }
}

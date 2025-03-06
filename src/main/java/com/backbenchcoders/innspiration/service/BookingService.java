package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.BookingDto;
import com.backbenchcoders.innspiration.dto.BookingRequest;
import com.backbenchcoders.innspiration.dto.GuestDto;

import java.util.List;

public interface BookingService {
    BookingDto initiateBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDto);
}

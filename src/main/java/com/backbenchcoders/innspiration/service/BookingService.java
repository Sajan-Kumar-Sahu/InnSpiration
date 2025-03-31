package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.dto.BookingDto;
import com.backbenchcoders.innspiration.dto.BookingRequest;
import com.backbenchcoders.innspiration.dto.GuestDto;
import com.backbenchcoders.innspiration.dto.HotelReportDto;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BookingService {
    BookingDto initiateBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDto);

    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);

    List<BookingDto> getAllBookingsByHotelId(Long hotelId);

    HotelReportDto getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDto> getMyBookings();

    BookingDto getBookingById(Long bookingId);
}

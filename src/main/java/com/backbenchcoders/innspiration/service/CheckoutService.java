package com.backbenchcoders.innspiration.service;

import com.backbenchcoders.innspiration.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}

package com.backbenchcoders.innspiration.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class HotelContactInfo {
    private String address;
    private String phone_number;
    private String email;
    private String location;
}

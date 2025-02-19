package com.backbenchcoders.innspiration.dto;

import com.backbenchcoders.innspiration.entity.HotelContactInfo;
import lombok.Data;

@Data
public class HotelDto {
    private Long id;

    private String name;

    private String city;

    private String[] images;

    private String[] amenities;

    private HotelContactInfo contactInfo;

    private Boolean isActive;
}

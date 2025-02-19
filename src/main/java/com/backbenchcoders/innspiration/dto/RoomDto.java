package com.backbenchcoders.innspiration.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomDto {

    private Long id;

    private String type;

    private BigDecimal basePrice;

    private String[] images;

    private String[] amenities;

    private Integer totalCount;

    private Integer capacity;
}

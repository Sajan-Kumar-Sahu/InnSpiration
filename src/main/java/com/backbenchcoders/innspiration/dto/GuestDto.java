package com.backbenchcoders.innspiration.dto;

import com.backbenchcoders.innspiration.entity.User;
import com.backbenchcoders.innspiration.entity.enums.Gender;
import lombok.Data;

import java.time.LocalDate;


@Data
public class GuestDto {
    private Long id;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
}

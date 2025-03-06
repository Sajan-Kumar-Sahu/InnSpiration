package com.backbenchcoders.innspiration.dto;

import com.backbenchcoders.innspiration.entity.User;
import com.backbenchcoders.innspiration.entity.enums.Gender;
import lombok.Data;


@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}

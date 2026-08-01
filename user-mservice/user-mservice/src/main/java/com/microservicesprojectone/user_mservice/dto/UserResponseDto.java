package com.microservicesprojectone.user_mservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String userName;
    private String email;
    private int age;
    private LocalDateTime timeOfJoining;

}

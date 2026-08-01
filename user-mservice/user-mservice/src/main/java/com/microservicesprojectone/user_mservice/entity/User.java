package com.microservicesprojectone.user_mservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long userId;
    private String userName;
    private String email;
    private int age;
    private LocalDateTime timeOfJoining;
    private String password;
    private UserRole userRole;

    private void setDefaultValues(){
        this.timeOfJoining=LocalDateTime.now();
    }


}

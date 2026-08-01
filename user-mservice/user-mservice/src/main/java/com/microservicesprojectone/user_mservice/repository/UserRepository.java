package com.microservicesprojectone.user_mservice.repository;

import com.microservicesprojectone.user_mservice.dto.UserResponseDto;
import com.microservicesprojectone.user_mservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

}

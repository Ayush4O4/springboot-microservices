package com.microservicesprojectone.user_mservice.service;

import com.microservicesprojectone.user_mservice.dto.UserLoginRequestDto;
import com.microservicesprojectone.user_mservice.dto.UserLoginResponseDto;
import com.microservicesprojectone.user_mservice.dto.UserRegisterRequestDto;
import com.microservicesprojectone.user_mservice.dto.UserResponseDto;
import com.microservicesprojectone.user_mservice.entity.User;
import com.microservicesprojectone.user_mservice.entity.UserRole;
import com.microservicesprojectone.user_mservice.jwt.JwtService;
import com.microservicesprojectone.user_mservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserResponseDto registerNewUser(UserRegisterRequestDto requestDto){
        User user=new User();
        user.setUserName(requestDto.getUserName());
        user.setAge(requestDto.getAge());
        String hashedPassword= passwordEncoder.encode(requestDto.getPassword());
        user.setPassword(hashedPassword);
        user.setEmail(requestDto.getEmail());
        user.setUserRole(UserRole.USER);


        User savedUser=userRepository.save(user);
        return new UserResponseDto(savedUser.getUserName(),savedUser.getEmail()
        , savedUser.getAge(),savedUser.getTimeOfJoining());
    }

    public UserLoginResponseDto loginExistingUser(UserLoginRequestDto userLoginRequestDto){
        User user=userRepository.findByEmail(userLoginRequestDto.getEmail())
                .orElseThrow(()->new RuntimeException("user not found"));
        if(!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())){
            throw new RuntimeException("wrong password");
        }

        return new UserLoginResponseDto(jwtService.generateJWT(user));

    }


}

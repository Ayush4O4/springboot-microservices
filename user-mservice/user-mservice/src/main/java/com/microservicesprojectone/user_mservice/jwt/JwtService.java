package com.microservicesprojectone.user_mservice.jwt;

import com.microservicesprojectone.user_mservice.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateJWT(User user){
        return Jwts.builder()
                .subject(user.getUserName())
                .claim("role",user.getUserRole())
                .claim("userId",user.getUserId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+86400000))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}

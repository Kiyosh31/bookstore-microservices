package com.kiyoshi.authservice.service.impl;

import com.kiyoshi.authservice.entity.TokenRequest;
import com.kiyoshi.authservice.entity.TokenResponse;
import com.kiyoshi.authservice.entity.User;
import com.kiyoshi.authservice.exception.BadRequestException;
import com.kiyoshi.authservice.exception.ResourceNotFoundException;
import com.kiyoshi.authservice.repository.AuthRepository;
import com.kiyoshi.authservice.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt-secret.key}")
    private String SECRET_KEY;


    @Value("${jwt-secret.expiration-time}")
    private String EXPIRATION_TIME;

    @Override
    public TokenResponse generateToken(TokenRequest request) {
        // verify the user
        Optional<User> found = repository.findUserByEmail(request.getEmail());
        if(found.isEmpty()){
            throw new ResourceNotFoundException("User not found", "email", request.getEmail());
        }

        // verify the password
        if(!passwordEncoder.matches(request.getPassword(), found.get().getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        // generate token
        return generateToken(request.getEmail());
    }

    @Override
    public Boolean validateToken(String authHeader) {
//        String bearer = authHeader.substring(7);
//        String email = getEmailFromToken(bearer);

        // verify the user
//        Optional<User> user = repository.findUserByEmail(email);
//        if(user.isEmpty()){
//            throw new ResourceNotFoundException("User not found", "email", email);
//        }

        //return user.get().getEmail().equals(email) && !isTokenExpired(bearer);
        return null;
    }

    private TokenResponse generateToken(String email) {
        Jwts.builder()
                .claims()
//        String token = Jwts.builder()
//                .subject(email)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + Integer.parseInt(EXPIRATION_TIME)))
//                .signWith(getKey())
//                .compact();
//
//        return TokenResponse.builder()
//                .token(token)
//                .build();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//    private String getEmailFromToken(String token) {
//        return getClaim(token, Claims::getSubject);
//    }

//    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = getAllClaims(token);
//        return claimsResolver.apply(claims);
//    }

//    private Claims getAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(SECRET_KEY)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }

//    private Boolean isTokenExpired(String token) {
//        return getExpiration(token).before(new Date());
//    }
//
//    private Date getExpiration(String token) {
//        return getClaim(token, Claims::getExpiration);
//    }
}

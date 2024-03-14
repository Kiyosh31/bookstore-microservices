package com.kiyoshi.authservice.service.impl;

import com.kiyoshi.authservice.entity.TokenRequest;
import com.kiyoshi.authservice.entity.TokenResponse;
import com.kiyoshi.authservice.entity.User;
import com.kiyoshi.authservice.exception.BadRequestException;
import com.kiyoshi.authservice.exception.ResourceNotFoundException;
import com.kiyoshi.authservice.repository.AuthRepository;
import com.kiyoshi.authservice.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    private Long EXPIRATION_TIME;

    @Override
    public TokenResponse generateToken(TokenRequest request) {
        // verify user exist
        Optional<User> existingUser = repository.findUserByEmail(request.getEmail());
        if(existingUser.isEmpty()){
            throw new ResourceNotFoundException("User not found", "email", request.getEmail());
        }

        // verify the password
        if(!passwordEncoder.matches(request.getPassword(), existingUser.get().getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        // generate token
        String token = generateToken(existingUser.get());

        // return
        return TokenResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public Boolean validateToken(String authHeader) {
        // get token
        String token = authHeader.substring(7);

        // get email from jwt
        String email = getEmailFromToken(token);

        // verify user exist
        Optional<User> existingUser = repository.findUserByEmail(email);
        if(existingUser.isEmpty()){
            throw new ResourceNotFoundException("User not found", "email", email);
        }

        // validate token
        return isTokenValid(token, existingUser.get());
    }

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    private String generateToken(Map<String,Object> extraClaims, User user) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .claim("username", user.getName())
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getKey())
                .header()
                .add("typ", "JWT")
                .and()
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes=Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmailFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, User user) {
        final String email=getEmailFromToken(token);
        return (email.equals(user.getEmail()) && !isTokenExpired(token));
    }

    private Claims getAllClaims(String token)
    {
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims,T> claimsResolver)
    {
        final Claims claims=getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token)
    {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token)
    {
        return getExpiration(token).before(new Date());
    }
}

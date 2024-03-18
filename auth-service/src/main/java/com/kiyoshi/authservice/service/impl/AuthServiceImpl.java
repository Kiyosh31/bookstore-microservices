package com.kiyoshi.authservice.service.impl;

import com.kiyoshi.authservice.entity.TokenRequest;
import com.kiyoshi.authservice.entity.TokenResponse;
import com.kiyoshi.authservice.entity.collection.User;
import com.kiyoshi.authservice.exception.ResourceNotFoundException;
import com.kiyoshi.authservice.repository.UserRepository;
import com.kiyoshi.authservice.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt-secret.key}")
    private String SECRET_KEY;

    @Value("${jwt-secret.minutes-expiration-time}")
    private Long MINUTES_EXPIRATION;

    @Override
    public TokenResponse loginUser(TokenRequest request) {
        // check if user exists
        Optional<User> found = repository.findUserByEmail(request.getEmail());
        if(found.isEmpty()) {
            throw new ResourceNotFoundException("User", "email", request.getEmail());
        }

        // validate password match
        if(!passwordEncoder.matches(request.getPassword(), found.get().getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = createToken(found.get());

        return TokenResponse.builder()
                .message("User authenticated successfully")
                .token(token)
                .statusCode(HttpStatus.OK)
                .build();
    }

    @Override
    public TokenResponse validateToken(String token) {
        String email = getEmailFromToken(token);

        // check if user exists
        Optional<User> found = repository.findUserByEmail(email);
        if(found.isEmpty()) {
            throw new ResourceNotFoundException("User", "email", email);
        }

        // user is not deleted
        if(!found.get().getIsActive()) {
            throw new ResourceNotFoundException("User", "email", email);
        }

        //still valid
        TokenResponse response = new TokenResponse();
        response.setToken(null);

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            response.setMessage("Authenticated successfully");
            response.setStatusCode(HttpStatus.OK);
            response.setToken(token);
        } catch (ExpiredJwtException e) {
            response.setMessage("Jwt expired");
            response.setStatusCode(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setMessage("Internal Server Error -> Error: " + e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    public String createToken(User user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + MINUTES_EXPIRATION * 60000); // Convert minutes to milliseconds

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
//                .claim("authorities", user.getAuthorities())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSecretKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

//    public List<String> getAuthoritiesFromToken(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(getSecretKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return claims.get("authorities", List.class);
//    }

    private SecretKey getSecretKey() {
        // We are using a fixed key here, but you should ideally load it from a secure location
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}

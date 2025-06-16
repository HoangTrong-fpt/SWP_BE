package com.quitsmoking.platform.service;


import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class TokenService {
    @Autowired
    AuthenticationRepository authenticationRepository;

    private final String SECRET_KEY = "4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d7490630407c";

    private SecretKey getSigninKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(Account account) {
        String token =
                // create object of JWT
                Jwts.builder()
                        .subject(account.getUsername())
                        .claim("role", account.getRole().name())
                        .claim("authorities", java.util.List.of("ROLE_" + account.getRole().name()))
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 1 ngày
                        .signWith(getSigninKey())
                        .compact();
        return token;
    }

    // form token to Claim Object
    public Claims extractAllClaims(String token) {
        return  Jwts.parser().
                verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // get userName form CLAIM
    public Account extractAccount(String token){
        Claims claims = extractAllClaims(token);

        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        // Tạo Account tạm thời có role (đủ để Spring tạo Authorities)
        Account account = new Account();
        account.setUsername(username);
        account.setRole(Role.valueOf(role));

        return account;
    }



    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    // get Expiration form CLAIM
    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    // from claim and extract specific data type.
    public <T> T extractClaim(String token, Function<Claims,T> resolver){
        Claims claims = extractAllClaims(token);
        return  resolver.apply(claims);
    }
}



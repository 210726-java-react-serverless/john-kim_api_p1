package com.revature.johnKimAPI.web.util.security;

import com.revature.johnKimAPI.web.dtos.Principal;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import java.util.Date;

/**
 * TokenGenerator is used to generate JSON Web Tokens, useful for the validation of users and ensuring that
 * they never stay valuable for long due to time limitations.
 */

public class TokenGenerator {

    private final JwtConfig jwtConfig;

    public TokenGenerator(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public JwtConfig getJwtConfig() {
        return jwtConfig;
    }

    public String generateToken(Principal subject) {
        long now = System.currentTimeMillis();

        JwtBuilder tokenBuilder = Jwts.builder()
                .setId(subject.getId())
                .setSubject(subject.getUsername())
                .claim("role", subject.isRole())
                .claim("lastName", subject.getLastName())
                .setIssuer("revature")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration()))
                .signWith(jwtConfig.getSigAlg(), jwtConfig.getSigningKey());

        return jwtConfig.getPrefix() + tokenBuilder.compact();


    }
}

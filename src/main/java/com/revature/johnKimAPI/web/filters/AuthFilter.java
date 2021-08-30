package com.revature.johnKimAPI.web.filters;

import com.revature.johnKimAPI.web.dtos.Principal;
import com.revature.johnKimAPI.web.util.security.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter extends HttpFilter {

    private final JwtConfig jwtConfig;
    private final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    public AuthFilter(JwtConfig jwtConfig) {
        System.out.println("Authfilter Instantiated!");
        this.jwtConfig = jwtConfig;
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        System.out.println("Request intercepted by AuthFilter!");
        parseToken(req);
        chain.doFilter(req, res);
    }

    public void parseToken(HttpServletRequest req) {
        try {

            String header = req.getHeader(jwtConfig.getHeader());

            System.out.println("Header value: " + header);

            if(header == null || !header.startsWith(jwtConfig.getPrefix())) {
                System.out.println("Request originates from an unauthenticated source!");
                logger.warn("Request originates from unauthorized source!");
                return;
            }

            String token = header.replaceAll(jwtConfig.getPrefix(), "");

            Claims jwtClaims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
            req.setAttribute("principal", new Principal(jwtClaims));
            System.out.println("Principal added as attribute to request!");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

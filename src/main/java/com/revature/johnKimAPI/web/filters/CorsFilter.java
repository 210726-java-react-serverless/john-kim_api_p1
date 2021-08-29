package com.revature.johnKimAPI.web.filters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CorsFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        req.setAttribute("filtered", true);
        resp.setHeader("Access-Control-Allow-Origin", "http://john-callahan-project-1.s3-website-us-east-1.amazonaws.com");
//        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Expose-Headers", "Authorization");
        chain.doFilter(req, resp);
    }
}

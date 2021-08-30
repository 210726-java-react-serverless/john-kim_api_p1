package com.revature.johnKimAPI.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.johnKimAPI.service.ValidationService;
import com.revature.johnKimAPI.util.exceptions.InvalidRequestException;
import com.revature.johnKimAPI.util.exceptions.ResourcePersistenceException;
import com.revature.johnKimAPI.web.dtos.Credentials;
import com.revature.johnKimAPI.web.dtos.ErrorResponse;
import com.revature.johnKimAPI.web.dtos.Principal;
import com.revature.johnKimAPI.web.util.security.TokenGenerator;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Request;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class FacServlet extends HttpServlet {

    private final ValidationService userService;
    private final ObjectMapper mapper;
    private final TokenGenerator generator;

    public FacServlet(ValidationService userService, ObjectMapper mapper, TokenGenerator generator) {
        this.userService = userService;
        this.mapper = mapper;
        this.generator = generator;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        Logger logger = LoggerFactory.getLogger(FacServlet.class);

        try {

            Credentials faculty = mapper.readValue(req.getInputStream(), Credentials.class);
            int hashPass = faculty.getPassword().hashCode();

            // Log the user in!
            Principal validFac = userService.facLogin(faculty.getUsername(), hashPass);

            // Send them back a 200 with a "got it" message.
            String payload = mapper.writeValueAsString(validFac);
            resp.getWriter().write(payload);

            // JWT = JSON Web Token
            String token = generator.generateToken(validFac);
            resp.setHeader(generator.getJwtConfig().getHeader(), token);

            logger.info("JWT Successfully created for Faculty!");

        } catch (InvalidRequestException | MismatchedInputException e) {
            e.printStackTrace();
            resp.setStatus(400); //client's fault
            ErrorResponse errResp = new ErrorResponse(400, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (ResourcePersistenceException rpe) {
            resp.setStatus(409);
            ErrorResponse errResp = new ErrorResponse(409, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500); // Server's fault
        }

    }


}

package com.revature.johnKimAPI.web.servlet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.johnKimAPI.pojos.Enrolled;
import com.revature.johnKimAPI.service.ValidationService;
import com.revature.johnKimAPI.util.exceptions.InvalidRequestException;
import com.revature.johnKimAPI.util.exceptions.ResourceNotFoundException;
import com.revature.johnKimAPI.util.exceptions.ResourcePersistenceException;
import com.revature.johnKimAPI.web.dtos.ErrorResponse;
import com.revature.johnKimAPI.web.dtos.Principal;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class EnrollServlet extends HttpServlet {

    private final ValidationService userService;
    private final ObjectMapper mapper;

    public EnrollServlet(ValidationService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        // Get the session from the request
        Principal requestingUser = (Principal)req.getAttribute("principal");

        try {
            if(requestingUser.getUsername() != null) {
                List<Enrolled> enrolled = userService.getMyCourses(requestingUser.getUsername());
                respWriter.write(mapper.writeValueAsString(enrolled));
            } else {
                ErrorResponse errResp = new ErrorResponse(400, "You are not logged in, and have no valid username!");
                respWriter.write(mapper.writeValueAsString(errResp));
            }
        } catch (ResourceNotFoundException rnfe) {
            resp.setStatus(404);
            ErrorResponse errResp = new ErrorResponse(404, "No course found!");
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            ErrorResponse errResp = new ErrorResponse(500, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        // Get the session from the request
        Principal requestingUser = (Principal)req.getAttribute("principal");

        String cancel = req.getParameter("cancel");


        try {
            if (requestingUser!=null) {
            if (cancel != null) {

                Enrolled enrolled = mapper.readValue(req.getInputStream(), Enrolled.class);
                userService.deregister(enrolled.getClassID(), enrolled.getUsername());

                ErrorResponse respInfo = new ErrorResponse(204, "Course canceled!");
                resp.getWriter().write(mapper.writeValueAsString(respInfo));

            } else {

                Enrolled newEnrolled = mapper.readValue(req.getInputStream(), Enrolled.class);

                userService.enroll(newEnrolled);

                ErrorResponse respInfo = new ErrorResponse(201, "Course registered!");
                resp.getWriter().write(mapper.writeValueAsString(respInfo));

            }
            } else {
                ErrorResponse errResp = new ErrorResponse(403, "You are not logged in, or have no valid JWT!");
                resp.getWriter().write(mapper.writeValueAsString(errResp));
            }
        } catch(JsonParseException jpe) {
            jpe.printStackTrace();
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(401, jpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch(InvalidRequestException | MismatchedInputException e) {
            e.printStackTrace();
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(400, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch(ResourcePersistenceException rpe) {
            resp.setStatus(409);
            ErrorResponse errResp = new ErrorResponse(409, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch(Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            ErrorResponse errResp = new ErrorResponse(500, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        }
    }
}

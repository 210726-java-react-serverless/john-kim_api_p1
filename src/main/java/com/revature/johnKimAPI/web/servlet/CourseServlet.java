package com.revature.johnKimAPI.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.johnKimAPI.pojos.Course;
import com.revature.johnKimAPI.service.ValidationService;
import com.revature.johnKimAPI.util.exceptions.InvalidRequestException;
import com.revature.johnKimAPI.util.exceptions.ResourceNotFoundException;
import com.revature.johnKimAPI.util.exceptions.ResourcePersistenceException;
import com.revature.johnKimAPI.web.dtos.ErrorResponse;
import com.revature.johnKimAPI.web.dtos.Principal;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * The purpose of the CourseServlet is to interact with Faculty-created courses, allowing queries to choose how they
 * are Read, Created, Updated, or Deleted.
 */

public class CourseServlet extends HttpServlet {

    private final ValidationService authService;
    private final ObjectMapper mapper;

    public CourseServlet(ValidationService authService, ObjectMapper mapper) {
        this.authService = authService;
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter respWriter = resp.getWriter();
        resp.setContentType("application/json");

        // Get the session from the request if it exists (This does not create a new session.)
        Principal requestingUser = (Principal)req.getAttribute("principal");

        // Filter the query to see if the Session is Authorized to make this kind of request.
        if (requestingUser == null) {
            resp.setStatus(401);
            ErrorResponse errResp = new ErrorResponse(401, "You are not currently logged in. Please log in!");
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        }

        // Establish query parameters
        String lookingForOpen = req.getParameter("open");
        String courseID = req.getParameter("classID");

        // Attempt to fulfill user request.
        try {
            if(lookingForOpen != null) {
                List<Course> openCourses = authService.getOpenClasses();
                respWriter.write(mapper.writeValueAsString(openCourses));
            } else if(courseID != null) {
                Course foundCourse = authService.getCourseByID(courseID);
                respWriter.write(mapper.writeValueAsString(foundCourse));
            } else if (!requestingUser.isRole()) {
                resp.setStatus(403);
                ErrorResponse errResp = new ErrorResponse(403, "You are not currently signed in as a Faculty member. Please make a valid query.");
                respWriter.write(mapper.writeValueAsString(errResp));
            } else {
                List<Course> courses = authService.getTeacherClasses(requestingUser.getLastName());
                respWriter.write(mapper.writeValueAsString(courses));
            }
        } catch (ResourceNotFoundException rnfe) {
            resp.setStatus(404);
            ErrorResponse errResp = new ErrorResponse(404, "We could not find any courses for you!");
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

        // Get the session from the request if it exists (This does not create a new session.)
        Principal requestingUser = (Principal)req.getAttribute("principal");

        // Filter the query to see if the Session is Authorized to make this kind of request.
        if (requestingUser == null) {
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(401, "You are not currently logged in. Please log in!");
            respWriter.write(mapper.writeValueAsString(errResp));
            return;
        } else if (!requestingUser.isRole()) {
            resp.setStatus(403);
            ErrorResponse errResp = new ErrorResponse(403, "You are not currently signed in as a Faculty member.");
            respWriter.write(mapper.writeValueAsString(errResp));
        }

        String delete = req.getParameter("delete");
        String update = req.getParameter(("update"));

        try {
            Course course = mapper.readValue(req.getInputStream(), Course.class);

            if(delete != null) {
                // Notify the user of your goal.
                System.out.println("Attempting to delete course " + course.getClassID() + "...");
                authService.deleteCourse(course.getClassID());

                // Send them back a 200 to denote success.
                ErrorResponse infoResp = new ErrorResponse(204, "Course successfully deleted!");
                resp.getWriter().write(mapper.writeValueAsString(infoResp));
            } else if(update != null) {
                // Notify the user of your goal.
                System.out.println("Attempting to update course " + course.getClassID() + "...");
                authService.updateCourse(course, course.getClassID());

                // Send them back a 200 to denote success.
                ErrorResponse infoResp = new ErrorResponse(201, "Course successfully updated!");
                resp.getWriter().write(mapper.writeValueAsString(infoResp));
            } else {
                // Attempt to persist the new course to the Database.
                authService.createCourse(course);

                // Send them back a 200 to denote success.
                ErrorResponse infoResp = new ErrorResponse(201, "Course successfully added!");
                resp.getWriter().write(mapper.writeValueAsString(infoResp));
            }
        } catch(InvalidRequestException | MismatchedInputException e) {
            e.printStackTrace();
            resp.setStatus(400);
            ErrorResponse errResp = new ErrorResponse(400, e.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch(ResourcePersistenceException rpe) {
            resp.setStatus(409);
            ErrorResponse errResp = new ErrorResponse(409, rpe.getMessage());
            respWriter.write(mapper.writeValueAsString(errResp));
        } catch(Exception e) { // For some reason, the execution failed.
            e.printStackTrace();
            resp.setStatus(500);
        }
    }
}

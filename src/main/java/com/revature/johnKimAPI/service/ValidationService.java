package com.revature.johnKimAPI.service;

import com.revature.johnKimAPI.pojos.Course;
import com.revature.johnKimAPI.pojos.Enrolled;
import com.revature.johnKimAPI.util.exceptions.InvalidRequestException;
import com.revature.johnKimAPI.pojos.Faculty;
import com.revature.johnKimAPI.pojos.Student;
import com.revature.johnKimAPI.repositories.SchoolRepository;
import com.revature.johnKimAPI.util.exceptions.ResourcePersistenceException;
import com.revature.johnKimAPI.web.dtos.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

import java.util.regex.Pattern;

/**
 *  A service-layer middleman that validates input before passing it to the database class responsible for
 *  placing it into the database. It also does Session Tracking.
 */

public class ValidationService {

        private final SchoolRepository schoolRepo;

        public ValidationService(SchoolRepository studentRepo){ this.schoolRepo = studentRepo; }

        Logger logger = LoggerFactory.getLogger(ValidationService.class);
        private boolean isValid = true;
        private Student authStudent;
        private Faculty authFac;


        public Student register(Student newStudent) {

                if (!isUserValid(newStudent)) {
                        throw new InvalidRequestException("Invalid user data provided!");

                }
                schoolRepo.save(newStudent);
                logger.info("New Student registered!" + newStudent);

                return newStudent;
        }

        // This will attempt to persist a created course to the courses database.
        public void createCourse(Course newCourse) {
                try {
                        // Verify that the class ID is not already taken.
                        if (isClassIDTaken(newCourse.getClassID()))
                                throw new InvalidRequestException("Class ID cannot already be taken!");

                        newCourse.setTeacher(this.authFac.getLastName());

                        if (isCourseValid(newCourse)) {
                                schoolRepo.newCourse(newCourse);
                                logger.info("New Course created!" + newCourse);
                        }
                } catch(InvalidRequestException ire) {
                        System.out.println(ire.getMessage());
                }
        }

        // This enrolls a student into a course, grafting their username to it,
        // and placing it within the separate 'enrolled' database.
        public void enroll(Enrolled course) {
                schoolRepo.enroll(course);
        }

        // Wipes user data and sets the session to an invalid one.
        public void logout() {
                this.isValid = false;
                this.authStudent = null;
                this.authFac = null;
        }

        public void setAuthStudent(Student authStudent) {
                this.authStudent = authStudent;
        }

        public void setAuthFac(Faculty authFac) {
                this.authFac = authFac;
        }

        public Principal login(String username, int hashPass) {
                if (username == null || username.trim().equals("")) {
                        throw new InvalidRequestException("Invalid user credentials provided!");
                }

                // This ensures that the session is marked 'valid'.
                this.isValid = true;

                Student authStudent = schoolRepo.findStudentByCredentials(username, hashPass);
                this.authStudent = authStudent;

                return new Principal(authStudent);
        }

        // Sends Student data to the requested location
        public Student getStudent() {
                if(this.authStudent != null || this.isValid) {
                        return this.authStudent;
                } else {
                        return null;
                }
        }

        public Principal facLogin(String username, int hashPass) {
                if (username == null || username.trim().equals("")) {
                        throw new InvalidRequestException("Invalid user credentials provided!");
                }

                this.authFac = schoolRepo.findFacultyByCredentials(username, hashPass);
                return new Principal(authFac);
        }

        // Sends faculty user data to the requested location
        public Faculty getAuthFac() {
                if(this.authFac != null || this.isValid) {
                        return this.authFac;
                } else {
                        return null;
                }
        }

        // This returns one class at the user's request (for the purposes of an update function)
        public Course getCourseByID(String id) {
                return schoolRepo.findCourseByID(id); }

        // This returns all classes that are open for enrollment.
        public List<Course> getOpenClasses() { return schoolRepo.findCourseByOpen(); }

        // This returns all classes associated with a student username.
        public List<Enrolled> getMyCourses(String username) {
                return schoolRepo.findEnrolledByUsername(username);
        }

        // This fetches the list of classes associated with a certain teacher name.
        public List<Course> getTeacherClasses(String teacher) { return schoolRepo.findCourseByTeacher(teacher); }

        public void updateCourse(Course newCourse, String id) {
                if(isCourseValid(newCourse)) {
                        String teacher = this.authFac.getLastName();
                        schoolRepo.updateCourse(newCourse, id, teacher);
                } else {
                        throw new ResourcePersistenceException("New course not valid!");
                }
        }

        public boolean deleteCourse(String id) {
                try {
                        if (isClassIDValid(id) && isClassIDTaken(id)) {
                                schoolRepo.deleteCourse(id);
                                logger.info("Course " + id + " deleted!");
                                return true;
                        }
                } catch(InvalidRequestException ire) {
                        System.out.println(ire.getMessage());
                }
                return false;
        }

        public void deregister(String id, String username) {
                try {
                        if (isClassIDValid(id) && isClassIDTaken(id)) {
                                schoolRepo.deleteEnrolled(id, username);
                                logger.info("Course " + id + " owned by " + authStudent.getUsername() + " deleted!");
                        }
                } catch(InvalidRequestException ire) {
                        System.out.println(ire.getMessage());
                }
        }


        public boolean isCourseValid(Course course) {
                try {
                        if (course == null) return false;
                        if (course.getName() == null || course.getName().trim().equals(""))
                                throw new InvalidRequestException("Course name cannot be null");
                        if (course.getClassID() == null || course.getClassID().trim().equals(""))
                                throw new InvalidRequestException("Class ID cannot be blank or null");
                        if (course.getDesc() == null || course.getDesc().trim().equals(""))
                                throw new InvalidRequestException("Course description cannot be blank or null");
                        if (course.getTeacher() == null) throw new InvalidRequestException("Teacher cannot be null");

                        isClassIDValid(course.getClassID());

                        // Verify that the course description is up to par.
                        if (course.getDesc().length() < 20 && course.getDesc().length() > 50)
                                throw new InvalidRequestException("Course description must be at least twenty characters and no more than fifty characters");

                        // Verify that the course name is within valid boundaries.
                        if (course.getName().length() < 5 && course.getName().length() <= 20)
                                throw new InvalidRequestException("Course name must be at least five characters and less than or equal to twenty characters");
                } catch (InvalidRequestException ire) {
                        System.out.println(ire.getMessage());
                        return false;
                }
                return true;
        }

        // This verifies that students are valid and fit to be placed in the system.
        public boolean isUserValid(Student user) {
                Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");
                if (user == null) return false;
                if (user.getFirstName() == null || user.getFirstName().trim().equals("")) return false;
                if (user.getLastName() == null || user.getLastName().trim().equals("")) return false;
                if (user.getEmail() == null || user.getEmail().trim().equals("")) return false;
                if(!emailPattern.matcher(user.getEmail()).find()) return false;
                if (user.getUsername() == null || user.getUsername().trim().equals("")) return false;
                this.isUserUnique(user.getUsername());
                this.isEmailUnique(user.getEmail());
                return true;
        }

        public boolean isClassIDValid(String ClassID) {

                // Verify that the class ID is exactly six characters long.
                if (ClassID.length() != 6) throw new RuntimeException("Class ID must be exactly six characters long!");

                // Verify that all characters in ClassID are uppercase
                for (int i=0; i<ClassID.length(); i++) {
                        if (Character.isLowerCase(ClassID.charAt(i))) {
                                throw new InvalidRequestException("Class ID must be all capital letters!");
                        }
                }

                return true;
        }

        private boolean isClassIDTaken(String ClassID) {
                return schoolRepo.findCourseByID(ClassID) != null;
        }

        public boolean isUserUnique(String username) {
                return schoolRepo.findStudentByUsername(username) == null;
        }

        public boolean isEmailUnique(String email) {
                return schoolRepo.findStudentByEmail(email) == null;
        }
}

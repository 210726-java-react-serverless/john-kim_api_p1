package com.revature.johnKimAPI.web.dtos;

import com.revature.johnKimAPI.pojos.Course;
import com.revature.johnKimAPI.pojos.Faculty;
import com.revature.johnKimAPI.pojos.Student;
import io.jsonwebtoken.Claims;

import java.util.Objects;

public class Principal {

    private String id;
    private String username;
    private String lastName;
    private boolean role;

    public Principal() { super(); }

    public Principal(Student student) {
        this.id = student.getStudentID();
        this.username = student.getUsername();
        this.lastName = null;
        this.role = student.isRole();
    }

    public Principal(Faculty faculty) {
        this.id = faculty.getTeacherID();
        this.username = faculty.getUsername();
        this.lastName = faculty.getLastName();
        this.role = faculty.isRole();
    }

    public Principal(Claims jwtClaims) {
        this.id = jwtClaims.getId();
        this.username = jwtClaims.getSubject();
        this.lastName = jwtClaims.get("lastName", String.class);
        this.role = jwtClaims.get("role", Boolean.class);
    }

    public Principal(Course course){
        this.id = course.getClassID();
        this.username = course.getName();
    }

    public boolean isRole() {
        return role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Principal principal = (Principal) o;
        return role == principal.role && Objects.equals(id, principal.id) && Objects.equals(username, principal.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, role);
    }

    @Override
    public String toString() {
        return "Principal{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", isFaculty=" + role +
                '}';
    }
}

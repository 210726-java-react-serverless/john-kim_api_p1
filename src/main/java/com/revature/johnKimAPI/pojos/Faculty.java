package com.revature.johnKimAPI.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * This POJO is necessary because it stores Faculty user data. This prevents Student users from potentially
 * tricking MongoDB or other sources into thinking that they are an Administrator. By design, it is
 * impossible to 'register' a Faculty member, and they need to be inserted via JSON into the
 * MongoDB 'FacultyCredentials' database.
 */

@JsonIgnoreProperties (ignoreUnknown = true)
public class Faculty {

    private String teacherID;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private int hashPass;

    // public empty constructor is needed to retrieve the POJO
    public Faculty(){}

    public Faculty(String username, int password, String firstName, String lastName, String email) {
        this.username = username;
        this.hashPass = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public int getHashPass() {
        return hashPass;
    }

    public void setHashPass(int hashPass) {
        this.hashPass = hashPass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return hashPass == faculty.hashPass && Objects.equals(teacherID, faculty.teacherID) && Objects.equals(firstName, faculty.firstName) && Objects.equals(lastName, faculty.lastName) && Objects.equals(email, faculty.email) && Objects.equals(username, faculty.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teacherID, firstName, lastName, email, username, hashPass);
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "teacherID='" + teacherID + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", hashPass=" + hashPass +
                '}';
    }
}

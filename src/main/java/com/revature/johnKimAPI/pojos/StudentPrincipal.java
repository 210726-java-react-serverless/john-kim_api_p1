package com.revature.johnKimAPI.pojos;

import java.util.Objects;

public class StudentPrincipal {

    private String studentID;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String hashPass;

    // public empty constructor is needed to retrieve the POJO
    public StudentPrincipal(){}

    public StudentPrincipal(String username, String password, String firstName, String lastName, String email) {
        this.username = username;
        this.hashPass = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
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

    public String getPassword() {
        return hashPass;
    }

    public void setPassword(String password) {
        this.hashPass = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentPrincipal that = (StudentPrincipal) o;
        return Objects.equals(studentID, that.studentID) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(email, that.email) && Objects.equals(username, that.username) && Objects.equals(hashPass, that.hashPass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentID, firstName, lastName, email, username, hashPass);
    }

    @Override
    public String toString() {
        return "StudentPrincipal{" +
                "studentID='" + studentID + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + hashPass + '\'' +
                '}';
    }
}

package com.revature.johnKimAPI.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;

/**
 * This POJO is necessary because it stores user data, typically compiled through the 'StudentRegisterPage' Page.
 * This data is validated within the service layer then passed to the SchoolRepository for injection into the
 * 'StudentCredentials' database in MongoDB. Unused getters and setters are left in case of future
 * expansion on this program.
 */

@JsonIgnoreProperties (ignoreUnknown = true)
public class Student {

    private String studentID;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private int hashPass;

    // public empty constructor is needed to retrieve the POJO
    public Student(){}

    public Student(String username, int password, String firstName, String lastName, String email) {
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
        Student student = (Student) o;
        return hashPass == student.hashPass && Objects.equals(studentID, student.studentID) && Objects.equals(firstName, student.firstName) && Objects.equals(lastName, student.lastName) && Objects.equals(email, student.email) && Objects.equals(username, student.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentID, firstName, lastName, email, username, hashPass);
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentID='" + studentID + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", hashPass=" + hashPass +
                '}';
    }
}

package com.revature.johnKimAPI.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * This POJO is necessary because it is where the username of a student is stored and then persisted
 * to a separate 'enrolled' database inside of MongoDB. Standard courses need to be separate because
 * they aren't tagged with any specific student.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Enrolled {
    private String classID;
    private String username;
    private String name;
    private String desc;
    private String teacher;
    boolean isOpen;

    // Empty, no-args constructor is necessary for the POJO to be grabbed by Mongo.
    public Enrolled() {}

    public Enrolled(String username, String name, String id, String desc, String teacher) {
        this.username = username;
        this.name = name;
        this.classID = id;
        this.desc = desc;
        this.teacher = teacher;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
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
        Enrolled enrolled = (Enrolled) o;
        return isOpen == enrolled.isOpen && Objects.equals(classID, enrolled.classID) && Objects.equals(username, enrolled.username) && Objects.equals(name, enrolled.name) && Objects.equals(desc, enrolled.desc) && Objects.equals(teacher, enrolled.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classID, username, name, desc, teacher, isOpen);
    }

    @Override
    public String toString() {
        return "Enrolled{" +
                "classID='" + classID + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", teacher='" + teacher + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}

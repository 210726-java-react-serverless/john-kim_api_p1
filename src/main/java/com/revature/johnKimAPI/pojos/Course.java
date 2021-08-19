package com.revature.johnKimAPI.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * This POJO is necessary to keep track of the 'course' objects compiled by teachers and stored into the MongoDB.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {
    private String classID;
    private String name;
    private String desc;
    private String teacher;
    boolean isOpen;

    // Empty, no args constructor necessary for Mongo to grab the course object.
    public Course() {}

    public Course(String name, String id, String desc, String teacher, boolean isOpen) {
        this.name = name;
        this.classID = id;
        this.desc = desc;
        this.teacher = teacher;
        this.isOpen = isOpen;
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
        isOpen = open;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return isOpen == course.isOpen && Objects.equals(classID, course.classID) && Objects.equals(name, course.name) && Objects.equals(desc, course.desc) && Objects.equals(teacher, course.teacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classID, name, desc, teacher, isOpen);
    }

    @Override
    public String toString() {
        return "Course{" +
                "classID='" + classID + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", teacher='" + teacher + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}

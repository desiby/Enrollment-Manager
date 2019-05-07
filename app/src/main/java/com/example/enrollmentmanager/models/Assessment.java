package com.example.enrollmentmanager.models;

import java.time.LocalDate;

public class Assessment {

    private long id;
    private String name;
    private String courseTitle;
    private LocalDate due_date;

    public Assessment(long id, String name, String courseTitle, LocalDate due_date) {
        this.id = id;
        this.name = name;
        this.courseTitle = courseTitle;
        this.due_date = due_date;
    }

    public Assessment(String name, String courseTitle, LocalDate due_date) {
        this.name = name;
        this.courseTitle = courseTitle;
        this.due_date = due_date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public LocalDate getDue_date() {
        return due_date;
    }

    public void setDue_date(LocalDate due_date) {
        this.due_date = due_date;
    }
}
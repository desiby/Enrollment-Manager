package com.example.enrollmentmanager.models;

import java.time.LocalDate;

public class CourseDetails {

    private long id;
    private String title;
    private String termName;
    private LocalDate start_date;
    private LocalDate end_date;
    private String status;
    private String mentorName;
    private String mentorPhone;
    private String mentorEmail;
    private String optionalNotes;

    public CourseDetails(long id, String title, String termName, LocalDate start_date, LocalDate end_date,
                         String status, String mentorName, String mentorPhone,
                         String mentorEmail, String optionalNotes) {
        this.id = id;
        this.title = title;
        this.termName = termName;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
        this.mentorName = mentorName;
        this.mentorPhone = mentorPhone;
        this.mentorEmail = mentorEmail;
        this.optionalNotes = optionalNotes;
    }

    public CourseDetails(String title, String termName, LocalDate start_date, LocalDate end_date,
                         String status, String mentorName, String mentorPhone, String mentorEmail,
                         String optionalNotes) {
        this.title = title;
        this.termName = termName;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
        this.mentorName = mentorName;
        this.mentorPhone = mentorPhone;
        this.mentorEmail = mentorEmail;
        this.optionalNotes = optionalNotes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public String getMentorPhone() {
        return mentorPhone;
    }

    public void setMentorPhone(String mentorPhone) {
        this.mentorPhone = mentorPhone;
    }

    public String getMentorEmail() {
        return mentorEmail;
    }

    public void setMentorEmail(String mentorEmail) {
        this.mentorEmail = mentorEmail;
    }

    public String getOptionalNotes() {
        return optionalNotes;
    }

    public void setOptionalNotes(String optionalNotes) {
        this.optionalNotes = optionalNotes;
    }
}

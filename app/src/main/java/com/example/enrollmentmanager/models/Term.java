package com.example.enrollmentmanager.models;

import java.time.LocalDate;

public class Term {

    private long id;
    private String title;
    private LocalDate start_date;
    private LocalDate end_date;

    public Term() {
    }


    public Term(long id, String title, LocalDate start_date, LocalDate end_date) {
        this.id = id;
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Term(String title, LocalDate start_date, LocalDate end_date) {
        this.title = title;
        this.start_date = start_date;
        this.end_date = end_date;
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
}

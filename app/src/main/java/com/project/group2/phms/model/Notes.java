package com.project.group2.phms.model;

/**
 * Created by vishwath on 4/9/17.
 */

public class Notes {

    private String date;
    private String note;
    private String key;
    private String title;

    public Notes() {
    }

    public Notes(String date, String note, String key, String title) {
        this.date = date;
        this.note = note;
        this.key = key;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

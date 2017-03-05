package com.project.group2.phms.model;

/**
 * Created by vishwath on 2/24/17.
 */

public class Vitals {

    private String key;
    private String systolic;
    private String diastolic;
    private String glucose;
    private String cholesterol;
    private String date;

    public Vitals(){

    }

    public Vitals(String systolic, String diastolic, String glucose, String cholesterol, String date){

        this.systolic = systolic;
        this.diastolic = diastolic;
        this.glucose = glucose;
        this.cholesterol = cholesterol;
        this.date = date;
        this.key="";
    }

    public String getCholesterol() {
        return cholesterol;
    }

    public String getDiastolic() {
        return diastolic;
    }

    public String getGlucose() {
        return glucose;
    }

    public String getSystolic() {
        return systolic;
    }

    public String getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }

    public void setDiastolic(String diastolic) {
        this.diastolic = diastolic;
    }

    public void setGlucose(String glucose) {
        this.glucose = glucose;
    }

    public void setSystolic(String systolic) {
        this.systolic = systolic;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setKey(String key) {
        this.key = key;
    }

}

package com.project.group2.phms.model;

/**
 * Created by ramajseepha on 3/24/17.
 */

public class Appointments {

    private String date;
    private String doctorName;
    private String doctorSpecialization;
    private String phoneNumber;
    private String emailAddress;
    private String appointmentDate;
    private String appointmentTime;
    private String purpose;
    private String prescription;
    private String key;
    public Appointments(){

    }

    public Appointments(String doctorName, String doctorSpecialization, String phoneNumber, String emailAddress, String appointmentDate,String appointmentTime, String purpose, String prescription, String key, String date){
        this.doctorName = doctorName;
        this.doctorSpecialization = doctorSpecialization;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.date = date;
        this.purpose = purpose;
        this.prescription = prescription;
        this.key = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

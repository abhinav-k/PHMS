package com.project.group2.phms.model;

/**
 * Created by ramajseepha on 2/24/17.
 */

public class Medication {

    private String medicationName;
    private String dosage;
    private String startDate;
    private String endDate;
    private String initialTime;
    private String frequency;
    private String dateMed;
    private String key;
    
    public Medication(){

    }

    public Medication(String dateMed, String medicationName, String dosage, String startDate, String endDate, String initialTime, String frequency) {
        this.medicationName = medicationName;
        this.dateMed = dateMed;
        this.dosage = dosage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialTime = initialTime;
        this.frequency = frequency;
        this.key = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(String initialTime) {
        this.initialTime = initialTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDateMed() {
        return dateMed;
    }

    public void setDateMed(String dateMed) {
        this.dateMed = dateMed;
    }
}

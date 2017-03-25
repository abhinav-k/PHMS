package com.project.group2.phms.model;

/**
 * Created by vishwath on 3/24/17.
 */

public class DesigneeDoctor {
    private String doctorName;
    private String doctorPhone;
    private String doctorEmail;
    private String designeeName;
    private String designeePhone;
    private String designeeEmail;

    public DesigneeDoctor(){

    }

    public DesigneeDoctor(String doctorName, String doctorPhone, String doctorEmail, String designeeName, String designeePhone, String designeeEmail) {
        this.doctorName = doctorName;
        this.doctorPhone = doctorPhone;
        this.doctorEmail = doctorEmail;
        this.designeeName = designeeName;
        this.designeePhone = designeePhone;
        this.designeeEmail = designeeEmail;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public String getDesigneeName() {
        return designeeName;
    }

    public void setDesigneeName(String designeeName) {
        this.designeeName = designeeName;
    }

    public String getDesigneePhone() {
        return designeePhone;
    }

    public void setDesigneePhone(String designeePhone) {
        this.designeePhone = designeePhone;
    }

    public String getDesigneeEmail() {
        return designeeEmail;
    }

    public void setDesigneeEmail(String designeeEmail) {
        this.designeeEmail = designeeEmail;
    }
}

package com.project.group2.phms.model;

/**
 * Created by vishwath on 2/12/17.
 */

public class User {
    private String name;
    private String userId;
    private String email;
    private String age;
    private String gender;
    private String weight;
    private String height;
    private String profile;


    public User() {

    }

    public User(String email, String name, String gender, String age, String weight, String height, String profile){
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.profile = profile;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getAge(){
        return age;
    }

    public String getGender(){
        return gender;
    }

    public String getWeight(){
        return weight;
    }

    public String getHeight(){
        return height;
    }

    public String getProfile() {
        return profile;
    }
}

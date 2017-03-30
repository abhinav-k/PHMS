package com.project.group2.phms.model;

/**
 * Created by vishwath on 3/29/17.
 */

public class Snacks {

    private String brandName;
    private String foodDescription;
    private String servingSize;
    private String calories;
    private String date;
    private String key;

    public Snacks(){

    }

    public Snacks(String brandName, String foodDescription, String servingSize, String calories, String date, String key){
        this.brandName = brandName;
        this.foodDescription = foodDescription;
        this.servingSize = servingSize;
        this.calories = calories;
        this.date = date;
        this.key = "";
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

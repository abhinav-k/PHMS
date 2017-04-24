package com.project.group2.phms.model;

/**
 * Created by vishwath on 4/23/17.
 */

public class Food {
    private String foodName;
    private String brandName;
    private int servingSize;
    private int calories;

    public Food(){

    }

    public Food(String foodName, String brandName, int servingSize, int calories) {
        this.foodName = foodName;
        this.brandName = brandName;
        this.servingSize = servingSize;
        this.calories = calories;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public int getServingSize() {
        return servingSize;
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}

package com.example.fooddetection;

import java.io.Serializable;

public class FoodModal implements Serializable {
     String foodName;
     int calories;

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public FoodModal(String foodName, int calories) {
        this.foodName = foodName;
        this.calories = calories;
    }
}

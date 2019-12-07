package com.daffodil.online.dietcoach.model;

public class Food {

    private String foodName;
    private String foodImage;
    private String quantity;
    private String quantityQualifier;
    private String foodType;

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantityQualifier() {
        return quantityQualifier;
    }

    public void setQuantityQualifier(String quantityQualifier) {
        this.quantityQualifier = quantityQualifier;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }
}

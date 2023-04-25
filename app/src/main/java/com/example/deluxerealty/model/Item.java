package com.example.deluxerealty.model;

import java.util.List;

public class Item {

    private String location;
    private String price;
    private String description;
    private String shortDescription;
    private String propertyType;
    private String image;
    private String id;
    private String user_id;


    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Item() {
    }

    public Item(String location, String price, String shortDescription) {
        this.location = location;
        this.price = price;
        this.shortDescription = shortDescription;
    }

    public Item(String location, String price, String description, String shortDescription, String image) {
        this.location = location;
        this.price = price;
        this.description = description;
        this.shortDescription = shortDescription;
        this.image = image;
    }

    public Item(String location, String price, String description, String shortDescription, String propertyType, String image, String user_id) {
        this.location = location;
        this.price = price;
        this.description = description;
        this.shortDescription = shortDescription;
        this.propertyType = propertyType;
        this.image = image;
        this.user_id = user_id;
    }

    public Item(String location, String price, String description, String shortDescription, String propertyType, String image, String id, String user_id) {
        this.location = location;
        this.price = price;
        this.description = description;
        this.shortDescription = shortDescription;
        this.propertyType = propertyType;
        this.image = image;
        this.id = id;
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

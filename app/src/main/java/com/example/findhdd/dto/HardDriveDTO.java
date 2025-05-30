package com.example.findhdd.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class HardDriveDTO implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("brand")
    private String brand;

    @SerializedName("model")
    private String model;

    @SerializedName("type")
    private String type;

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("formFactor")
    private String formFactor;

    @SerializedName("readSpeed")
    private int readSpeed;

    @SerializedName("writeSpeed")
    private int writeSpeed;

    @SerializedName("purpose")
    private String purpose;

    @SerializedName("interfaceType")
    private String interfaceType;

    @SerializedName("powerConsumption")
    private double powerConsumption;

    @SerializedName("warranty")
    private String warranty;

    @SerializedName("length")
    private double length;

    @SerializedName("width")
    private double width;

    @SerializedName("height")
    private double height;

    @SerializedName("weight")
    private int weight;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;
}
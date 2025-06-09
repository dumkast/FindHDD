package com.example.findhdd.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class HardDriveDTO implements Serializable {
    private Long id;
    private String brand;
    private String model;
    private String type;
    private int capacity;
    private String formFactor;
    private int readSpeed;
    private int writeSpeed;
    private String purpose;
    private String interfaceType;
    private double powerConsumption;
    private String warranty;
    private double length;
    private double width;
    private double height;
    private int weight;
    private String description;
    private double price;
}
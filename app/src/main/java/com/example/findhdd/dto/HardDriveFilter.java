package com.example.findhdd.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HardDriveFilter {
    private List<String> brands = new ArrayList<>();
    private List<String> types = new ArrayList<>();
    private List<String> purposes = new ArrayList<>();
    private List<String> formFactors = new ArrayList<>();
    private Double minPrice;
    private Double maxPrice;
    private Integer minCapacity;
    private Integer maxCapacity;

    public boolean isEmpty() {
        return (brands == null || brands.isEmpty())
                && (types == null || types.isEmpty())
                && (purposes == null || purposes.isEmpty())
                && (formFactors == null || formFactors.isEmpty())
                && minPrice == null
                && maxPrice == null
                && minCapacity == null
                && maxCapacity == null;
    }
}


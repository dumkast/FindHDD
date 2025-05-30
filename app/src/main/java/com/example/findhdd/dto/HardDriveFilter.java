package com.example.findhdd.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.util.List;

@Data
public class HardDriveFilter {
    @SerializedName("brands")
    private List<String> brands;

    @SerializedName("types")
    private List<String> types;

    @SerializedName("purposes")
    private List<String> purposes;

    @SerializedName("formFactors")
    private List<String> formFactors;

    @SerializedName("minPrice")
    private Double minPrice;

    @SerializedName("maxPrice")
    private Double maxPrice;

    @SerializedName("minCapacity")
    private Integer minCapacity;

    @SerializedName("maxCapacity")
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


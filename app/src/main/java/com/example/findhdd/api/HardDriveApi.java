package com.example.findhdd.api;

import com.example.findhdd.dto.HardDriveDTO;
import com.example.findhdd.dto.HardDriveFilter;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface HardDriveApi {
    @GET("hdd")
    Call<List<HardDriveDTO>> getAll();

    @GET("hdd/{id}")
    Call<HardDriveDTO> getById(@Path("id") Long id);

    @GET("hdd/search")
    Call<List<HardDriveDTO>> search(@Query("q") String query);

    @POST("hdd/filter")
    Call<List<HardDriveDTO>> filter(@Body HardDriveFilter filter);

    @GET("hdd/sort")
    Call<List<HardDriveDTO>> sort(@Query("direction") String direction);

    @POST("hdd")
    Call<HardDriveDTO> create(@Body HardDriveDTO hardDriveDTO);

    @DELETE("hdd/{id}")
    Call<ApiMessage> delete(@Path("id") Long id);

    @PUT("hdd/{id}")
    Call<HardDriveDTO> update(@Path("id") Long id, @Body HardDriveDTO hardDriveDTO);
}

package com.example.findhdd.api;

import com.example.findhdd.dto.ChangePasswordRequest;
import com.example.findhdd.dto.HardDriveDTO;
import com.example.findhdd.dto.UserDTO;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface UserApi {
    @GET("user/login")
    Call<ApiMessage> login();

    @POST("user/register")
    Call<ApiMessage> register(@Body Map<String, String> body);

    @POST("user/change-password")
    Call<ApiMessage> changePassword(@Body ChangePasswordRequest request);

    @GET("user/me")
    Call<UserDTO> getCurrentUser();

    @POST("user/favorites/{hddId}")
    Call<ApiMessage> addFavorite(@Path("hddId") Long hddId);

    @DELETE("user/favorites/{hddId}")
    Call<ApiMessage> removeFavorite(@Path("hddId") Long hddId);

    @GET("user/favorites")
    Call<List<HardDriveDTO>> getFavorites();
}

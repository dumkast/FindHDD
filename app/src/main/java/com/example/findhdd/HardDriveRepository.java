package com.example.findhdd;

import android.content.Context;
import android.location.GnssNavigationMessage;

import com.example.findhdd.api.ApiClient;
import com.example.findhdd.api.ApiMessage;
import com.example.findhdd.api.HardDriveApi;
import com.example.findhdd.dto.HardDriveDTO;
import com.example.findhdd.dto.HardDriveFilter;

import java.util.List;

import retrofit2.Callback;

public class HardDriveRepository {
    private final HardDriveApi hardDriveApi;

    public HardDriveRepository(Context context) {
        PreferencesManager prefs = new PreferencesManager(context);
        hardDriveApi = ApiClient.getClient(prefs.getUsername(), prefs.getPassword()).create(HardDriveApi.class);
    }

    public void getAllHardDrives(Callback<List<HardDriveDTO>> callback) {
        hardDriveApi.getAll().enqueue(callback);
    }

    public void addHardDrive(HardDriveDTO dto, Callback<HardDriveDTO> callback) {
        hardDriveApi.create(dto).enqueue(callback);
    }

    public void updateHardDrive(Long id, HardDriveDTO dto, Callback<HardDriveDTO> callback) {
        hardDriveApi.update(id, dto).enqueue(callback);
    }

    public void deleteHardDrive(Long id, Callback<ApiMessage> callback) {
        hardDriveApi.delete(id).enqueue(callback);
    }

    public void filterHardDrives(HardDriveFilter filter, Callback<List<HardDriveDTO>> callback) {
        hardDriveApi.filter(filter).enqueue(callback);
    }

    public void sortHardDrives(String direction, Callback<List<HardDriveDTO>> callback) {
        hardDriveApi.sort(direction).enqueue(callback);
    }
}
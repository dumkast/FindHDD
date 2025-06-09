package com.example.findhdd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.findhdd.api.ApiMessage;
import com.example.findhdd.activity.AuthActivity;
import com.example.findhdd.activity.MainActivity;
import com.example.findhdd.PreferencesManager;
import com.example.findhdd.R;
import com.example.findhdd.api.UserApi;
import com.example.findhdd.dto.ChangePasswordRequest;
import com.example.findhdd.dto.UserDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private EditText etCurrentPassword, etNewPassword;
    private Button btnChangePassword, btnLogout;
    private UserApi userApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        etCurrentPassword = view.findViewById(R.id.et_current_password);
        etNewPassword = view.findViewById(R.id.et_new_password);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);

        if (getActivity() instanceof MainActivity) {
            userApi = ((MainActivity) getActivity()).getUserApi();
        }

        loadUserData();
        btnChangePassword.setOnClickListener(v -> changePassword());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserData() {
        if (userApi == null) {
            Toast.makeText(getContext(), "API не инициализирован", Toast.LENGTH_SHORT).show();
            return;
        }

        userApi.getCurrentUser().enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDTO user = response.body();
                    tvUsername.setText(user.getUsername());
                    tvEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(getContext(), "Не удалось загрузить пользовательские данные", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(getContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

        userApi.changePassword(request).enqueue(new Callback<ApiMessage>() {
            @Override
            public void onResponse(Call<ApiMessage> call, Response<ApiMessage> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PreferencesManager prefs = new PreferencesManager(requireContext());
                    String username = prefs.getUsername();

                    prefs.saveCredentials(username, newPassword);

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).recreateApiClient(username, newPassword);
                    }

                    etCurrentPassword.setText("");
                    etNewPassword.setText("");
                    Toast.makeText(getContext(), "Пароль успешно изменен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Ошибка при смене пароля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiMessage> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        PreferencesManager prefs = new PreferencesManager(requireContext());
        prefs.clearCredentials();

        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}